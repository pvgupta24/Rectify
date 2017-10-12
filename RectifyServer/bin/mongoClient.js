var mongoClient = require('mongodb').MongoClient;

var mongo_client = {};

var state = {
    Db: null
};

mongo_client.connect = function (url, callback) {
    if (state.Db) {
        callback();
    }
    mongoClient.connect(url, function (err, db) {
        if (err)
            callback(err);
        state.Db = db;
        callback();
    });
};

mongo_client.get = function () {
    return state.Db;
};

mongo_client.close = function (callback) {
    if (state.Db) {
        state.Db.close(function (err, result) {
            state.Db = null;
            callback(err);
        });
    }
};

mongo_client.insertToDB = function (collection, userObj, callback) {
    collection.insert(userObj, callback);
};

mongo_client.updateToDB = function (collection, query, submissionObj, callback) {
    collection.update(query, submissionObj, {upsert : true, w:1}, callback);
};

mongo_client.findInDB = function (collection, userObj, start, count, callback) {
    collection.find(userObj, {skip: start}).limit(count).toArray(callback);  
};

mongo_client.getLeaders = function (collection, queryObj, sortObj, start, count, callback) {
    collection.find(queryObj, {skip: start}).sort(sortObj).limit(count).toArray(callback);
};

module.exports = mongo_client;