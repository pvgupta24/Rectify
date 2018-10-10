# Rectify Server

Rectify is a coding and debugging event in Engineer NITK.

## Dependencies

+ java
+ maven
+ nodejs
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

Install dependencies

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

Install dependencies

```
mvn package
```

Run the Judge

```
java -jar target/Judge-1.0-SNAPSHOT-jar-with-dependencies.jar 
```
