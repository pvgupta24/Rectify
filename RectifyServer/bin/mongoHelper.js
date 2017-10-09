var mongo_client = require('./mongoClient');
var cons = require('./constants');

var mongo_helper = {};

mongo_helper.AddUser = function (first_name, last_name, email, password, callback) {
    var userObj = {};
    userObj.first_name = first_name;
    userObj.last_name = last_name;
    userObj.email = email;
    userObj.password = password;
    var collection = mongo_client.get().collection(cons.UsersColl);
    mongo_client.insertToDB(collection, userObj, callback);
};

mongo_helper.FindUser = function (email, callback) {
    var userObj = {};
    userObj.email = email;
    var collection = mongo_client.get().collection(cons.UsersColl);
    mongo_client.findInDB(collection, userObj, 0, 100, callback);
};
module.exports = mongo_helper;