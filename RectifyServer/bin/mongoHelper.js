var mongo_client = require('./mongoClient');
var cons = require('./constants');

var mongo_helper = {};

mongo_helper.AddUser = function (first_name, last_name, email, password, user_id, callback) {
    var userObj = {};
    userObj._id = user_id;
    userObj.user_id = user_id;
    userObj.first_name = first_name;
    userObj.last_name = last_name;
    userObj.email = email;
    userObj.password = password;
    userObj.score = 0;
    var collection = mongo_client.get().collection(cons.UsersColl);
    mongo_client.insertToDB(collection, userObj, callback);
};

mongo_helper.FindUser = function (user_id, callback) {
    var userObj = {};
    userObj._id = user_id;
    userObj.user_id = user_id;
    var collection = mongo_client.get().collection(cons.UsersColl);
    mongo_client.findInDB(collection, userObj, 0, 100, callback);
};

mongo_helper.GetProblems = function (callback) {
    var problemObj = {};
    var collection = mongo_client.get().collection(cons.ProblemsColl);
    mongo_client.findInDB(collection, problemObj, 0, 10, callback);
};

mongo_helper.GetProblemById = function (problem_id, callback) {
    var problemObj = {};
    problemObj._id = problem_id;
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

mongo_helper.AddSubmission = function (problem_id, problem_name, user_id, code, timestamp, callback) {
    var submissionObj = {};
    submissionObj.problem_id = problem_id;
    submissionObj.problem_name = problem_name;
    submissionObj.user_id = user_id;
    submissionObj.code = code;
    submissionObj._id = user_id + "_" + problem_id;
    submissionObj.timestamp = timestamp;
    var collection = mongo_client.get().collection(cons.SubmissionsColl);
    var query = {};
    query._id = submissionObj._id;
    mongo_client.updateToDB(collection, query, submissionObj, callback);
};

mongo_helper.UpdateScore = function(user_id, score_change, callback) {
    var updateObj = {
        "$inc": {
            "score": score_change
        }
    };
    var query = {};
    query._id = user_id;
    var collection = mongo_client.get().collection(cons.UsersColl);
    mongo_client.updateToDB(collection, query, updateObj, callback);
};

mongo_helper.GetSubmission = function (problem_id, user_id, callback) {
    var submissionObj = {};
    submissionObj._id = user_id + "_" + problem_id;
    var collection = mongo_client.get().collection(cons.SubmissionsColl);
    mongo_client.findInDB(collection, submissionObj, 0, 1, callback);
};

mongo_helper.GetSubmissionsByUserId = function (user_id, callback) {
    var submissionObj = {};
    submissionObj.user_id = user_id;
    var collection = mongo_client.get().collection(cons.SubmissionsColl);
    mongo_client.findInDB(collection, submissionObj, 0, 100, callback);
};

mongo_helper.GetLeaderboard = function (callback) {
    var collection = mongo_client.get().collection(cons.UsersColl);
    // Add time.
    var sortObj = {score: -1};
    mongo_client.getLeaders(collection, {}, sortObj, 0, 100, callback);
};

mongo_helper.GetAllSubmissions = function (callback) {
    var collection = mongo_client.get().collection(cons.SubmissionsColl);
    var Obj = {};
    mongo_client.findInDB(collection, Obj, 0, 500, callback);
};

mongo_helper.GetSubmissionOfUser = function (user_id, problem_id, callback) {
    var collection = mongo_client.get().collection(cons.SubmissionsColl);
    var obj = {};
    obj.user_id = user_id;
    obj.problem_id = problem_id;
    mongo_client.findInDB(collection, obj, 0, 3, callback);
};

mongo_helper.AddHacks = function (user_id, problem_id, opponent_id, callback) {
    var collection = mongo_client.get().collection(cons.HacksColl);
    var obj = {};
    obj.user_id = user_id;
    obj.problem_id = problem_id;
    obj.opponent_id = opponent_id;
    mongo_client.insertToDB(collection, obj, callback);
};

mongo_helper.GetHacksByUser = function (user_id, callback) {
    var collection = mongo_client.get().collection(cons.HacksColl);
    var obj = {};
    obj.user_id = user_id;
    mongo_client.findInDB(collection, obj, 0, 100, callback);
};

module.exports = mongo_helper;