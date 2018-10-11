# Rectify Server

Rectify is a coding and debugging event in Engineer NITK.

## Dependencies

+ java8
+ maven
+ node.js
+ mongodb

## Build & Run

### MongoDB Server

Install `mongodb` package and enable the service

```
sudo systemctl enable mongodb.service
```

Then start the mongodb service

```
sudo systemctl start mongodb.service
```

### Rectify Server

Install dependencies in RectifyServer

```
npm install
```

Run the server

```
npm start
```

The site should be running on [localhost:3000](http://localhost:3000)

### Judge

Install `java` and `maven`

Install dependencies in Judge

```
mvn package
```

Run the Judge

```
java -jar target/Judge-1.0-SNAPSHOT-jar-with-dependencies.jar 
```

## Instructions for adding Questions

Create a folder named `Q<Q_no>`

Create a new file each in the folder for 
+ Problem Name in `name.txt`
+ Problem Statement in `statement.txt`
+ Problem Constraints in `constraints.txt`
+ Correct C++ code in `code.cpp`
+ Time Limit (in seconds without any units) in `time_limit.txt`
+ Memory Limit(in MBs without any units) in `memory_limit.txt`
+ Number of sample test cases (Simple ones without proper corner + cases so that people can hack the simple solutions) in + `nsimple.txt`
+ Number of system test cases (Good corner cases) in `nsystem.txt`

Create subfolders `simple` and `system` and add corresponding test cases as
+ Input files : `1.in` , `2.in`  etc
+ Output files : `1.out` , `2.out` etc    

See `sample_questions/q1` for example

Then add question to db

```
python add_problem.py <folder-name> <problem-id>
```

For example, to add `sample_questions/q1`

```
python add_problem.py sample_questions/q1 1 > out.sh && ./out.sh && rm out.sh
```
