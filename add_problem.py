import sys
import os
import subprocess
import shlex

DB = "RectifyTest"
HELP = 'Usage python add_problem.py <folder-name> <problem-id>'

if len(sys.argv) != 3:
    print(HELP)
    sys.exit(0)

def read_and_get(filename):
    file = open(filename, "r")
    return file.read().replace('"', '\\"').replace('\'', '\'\\\'\'')

def execute(command):
    print(command)
    # proc = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)#, stderr=subprocess.PIPE, stdin=subprocess.PIPE)
    # out, err = proc.communicate()
    # if proc.returncode != 0 or err is not None:
    #     print(out)
    #     print(err)
    #     print("Exiting due to error : {0}".format(proc.returncode))
    #     sys.exit(proc.returncode)
    # return out

def db_insert(document, entry):
    command = "echo 'db.{1}.insert({2});' | mongo {0}".format(
        DB, document, entry)
    execute(command)

def remove_duplicate(problem_id):
    print("Removing problem with id {0}".format(problem_id))
    query = '\"problem_id\": \"{0}\"'.format(problem_id)
    query = "{" + query + "}"
    command = "echo 'db.Problems.remove({1});' | mongo {0}".format(
        DB, query)
    execute(command)
    command = "echo 'db.Testcases.remove({1});' | mongo {0}".format(
        DB, query)
    execute(command)
    command = "echo 'db.SystemTests.remove({1});' | mongo {0}".format(
        DB, query)
    execute(command)

def check_for_duplicate(problem_id):
    query = '\"problem_id\": \"{0}\"'.format(problem_id)
    query = "{" + query + "}"
    command = "echo 'db.Problems.find({1});' | mongo {0}".format(
        DB, query)
    out = execute(command)
    if "problem_id" in str(out):
        choice = raw_input("Problem with id {0} already exists. Do you want to overwrite? [y/N] : ".format(problem_id))
        if choice == "y" or choice == "Y":
            remove_duplicate(problem_id)
        else:
            sys.exit(0)

question_folder = sys.argv[1]
problem_id = sys.argv[2]

check_for_duplicate(problem_id)

name = read_and_get(os.path.join(question_folder, "name.txt"))
statement = read_and_get(os.path.join(question_folder, "statement.txt"))
constraints = read_and_get(os.path.join(question_folder, "constraints.txt"))
code = read_and_get(os.path.join(question_folder, "code.cpp"))
time_limit = read_and_get(os.path.join(question_folder, "time_limit.txt"))
memory_limit = read_and_get(os.path.join(question_folder, "memory_limit.txt"))
nsimple = int(read_and_get(os.path.join(question_folder, "nsimple.txt")))
nsystem = int(read_and_get(os.path.join(question_folder, "nsystem.txt")))

problem_entry = """
{7}
    "_id": "{0}",
    "problem_id": "{0}",
    "problem_name": "{1}",
    "problem_statement": "{2}",
    "problem_constraints": "{3}",
    "time_constraint": "{4}",
    "memory_constraint": "{5}",
    "correct_code": "{6}"
{8}
""".format(
    problem_id,
    name.replace('\n', ''),
    statement.replace('\n', '<br/>'),
    constraints.replace('\n', '<br/>'),
    time_limit.replace('\n', ''),
    memory_limit.replace('\n', ''),
    code,#.replace('\n', ' '),
    "{",
    "}"
).replace('\n', '')

# print("Deleting Database")
# print("echo 'db.dropDatabase()' | mongo {0}".format(DB))

# print("Adding problem to db")

db_insert("Problems", problem_entry)

for i in range(1, nsimple + 1):
    in_ = read_and_get(os.path.join(question_folder,
                                    "simple", "{0}.in".format(i)))
    out = read_and_get(os.path.join(question_folder,
                                    "simple", "{0}.out".format(i)))
    simple_test_entry = """
    {4}
        "problem_id": "{0}", 
        "input_data": "{1}",
        "output_data": "{2}",
        "time_limit": "{3}"
    {5}
    """.format(
        problem_id,
        in_,
        out,
        time_limit,
        "{",
        "}"
    )
    # print("Adding simple test #{0} to db".format(i))
    db_insert("Testcases", simple_test_entry)

for i in range(1, nsystem + 1):
    in_ = read_and_get(os.path.join(question_folder,
                                    "system", "{0}.in".format(i)))
    out = read_and_get(os.path.join(question_folder,
                                    "system", "{0}.out".format(i)))
    system_test_entry = """
    {4}
        "problem_id": "{0}", 
        "input_data": "{1}",
        "output_data": "{2}",
        "time_limit": "{3}"
    {5}
    """.format(
        problem_id,
        in_,
        out,
        time_limit,
        "{",
        "}"
    )
    # print("Adding system test #{0} to db".format(i))
    db_insert("SystemTests", simple_test_entry)

os.system("cp {0} Solutions/{1}.cc".format(
    os.path.join(question_folder, "code.cpp"), problem_id))
# print("[DONE]") 
