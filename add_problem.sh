python add_problem.py $1 $2 $3

if [ $? -eq 69 ]
then
echo "bye"
exit
fi

for file in $(find $1 -name *.db_insert)
do
    echo "executing mongo " $3 " < " $file
    mongo $3 < $file
done