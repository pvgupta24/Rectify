var express = require('express');
var router = express.Router();
var request = require('request');
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');
var cons = require("../bin/constants.js");

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (meta_info.phase == cons.SYSTEM_TESTING_PHASE) {
        mongo_helper.GetAllSubmissions(function (err, results) {
            if (err) {
                res.status(500).json("Error getting all submissions.");
            } else {
                results.forEach(function (element) {
                     mongo_helper.GetAllSystemTest(element.problem_id, function (err, dbResults) {
                         var submissionObj = {};
                         submissionObj.userId = element.user_id;
                         submissionObj.code = element.code;
                         submissionObj.problemId = element.problem_id;
                         submissionObj.testcases = [];
                         dbResults.forEach(function(systemTest) {
                             var testObj = {};
                             testObj.input_data = systemTest.input_data;
                             testObj.output_data = systemTest.output_data;
                             submissionObj.testcases.push(testObj);
                             submissionObj.timeLimit = parseInt(systemTest.time_limit, 10);
                         });
                         request.post(
                             'http://0.0.0.0:9002/code/submit',
                             {json: submissionObj},
                             function (error, response, body) {
                                 if (error) {
                                     console.log("Error!");
                                     res.status(500).json("Error service.");
                                 } else {
                                     if (response.statusCode == 200) {
                                         // Successful. If successful update the score and store the submission for hacking.
                                         if (body.submissionStatus == "ACCEPTED") {
                                             var score = cons.SystemTestScore;
                                             mongo_helper.UpdateScore(submissionObj.userId, score, function (err, dbResults) {
                                                 if (err) {
                                                     res.status(500).json("Error saving your submission to the database. Error " + err);
                                                 } else {
                                                     console.log("Good. Updated scores.");
                                                 }
                                             });
                                         } else {
                                            console.log("System test failed.");
                                         }
                                     } else {
                                        console.log("Service unreachable.");
                                     }
                                 }
                             }
                         );
                     });
                });
                res.redirect('/leaderboard');
            }
        });
    }
});

module.exports = router;