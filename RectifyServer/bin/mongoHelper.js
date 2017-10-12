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

mongo_helper.GetProblems = function (callback) {
    var problemObj = {};
    var collection = mongo_client.get().collection(cons.ProblemsColl);
    mongo_client.findInDB(collection, problemObj, 0, 3, callback);
};

mongo_helper.GetProblemById = function (problem_id, callback) {
    var problemObj = {};
    problemObj.problem_id = problem_id;
    var collection = mongo_client.get().collection(cons.ProblemsColl);
    mongo_client.findInDB(collection, problemObj, 0, 1, callback);
};

mongo_helper.GetTestcases = function (problem_id, callback) {
    var problemObj = {};
    problemObj.problem_id = problem_id;
    var collection = mongo_client.get().collection(cons.TestcasesColl);
    mongo_client.findInDB(collection, problemObj, 0, 100, callback);
};

module.exports = mongo_helper;