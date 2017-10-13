var express = require('express');
var request = require('request');
var url = require('url');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');
var async = require('async');
var cons = require('../bin/constants');

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        var problem_id = req.query.problem_id;
        mongo_helper.GetProblemById(problem_id, function (err, dbResults) {
            if (err) {
                res.status(500).json("Error while getting problem. Error: ", err);
            } else {
                if (dbResults.length == 1) {
                    meta_info.problems = [];
                    dbResults.forEach(function (problem_info) {
                        meta_info.problems.push(problem_info);
                    });
                    res.render('challenge', {meta_data: meta_info});
                } else {
                    res.status(500).json("Error. Got more than 1 problem for same problem id.");
                }
            }
        });
    } else {
        res.redirect(url.format({
            pathname:"/login",
            query: {
                "err_message": "Please login."
            }
        }));
    }
});


router.post('/', function (req, res, next) {
    var meta_info = meta.getMeta();
    meta_info.is_logged_in = true;
    var problem_id = req.query.problem_id;
    var userObj = req.session.user;
    var submission = req.body.code;
    // Add timestamp.
    var timestamp = (new Date).getTime();
    var submissionObj = {};
    submissionObj.userId = userObj.user_id;
    submissionObj.code = submission;
    submissionObj.problemId = problem_id;
    submissionObj.testcases = [];
    var problemName;
    mongo_helper.GetTestcases(problem_id, function (err, dbResults) {
        if (err) {
            console.log("Error!", err);
            res.status(500).json("Error while fetching test cases.");
        } else {
            dbResults.forEach(function(element) {
                var testObj = {};
                problemName = element.problem_name;
                testObj.input_data = element.input_data;
                testObj.output_data = element.output_data;
                submissionObj.testcases.push(testObj);
                submissionObj.timeLimit = parseInt(element.time_limit, 10);
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
                                async.parallel([
                                    function (callback) {
                                        mongo_helper.AddSubmission(problem_id, problemName, 
                                        submissionObj.userId, submissionObj.code, timestamp, function (err, dbResults) {
                                               if (err) {
                                                   callback(err);
                                               }
                                               callback();
                                            });
                                    },
                                    function (callback) {
                                        var score = 0;
                                        if (req.query.solved == "no") {
                                            score = cons.SampleTestScore;
                                        }
                                        var time = timestamp - meta_info.contest_start_time;
                                        time = time / 1000;
                                        mongo_helper.UpdateScore(submissionObj.userId, score, time, function (err, dbResults) {
                                            if (err) {
                                                callback(err);
                                            }
                                            callback();
                                        })
                                    }
                                ], function (err, results) {
                                    if (err) {
                                        res.status(500).json("Error saving your submission to the database. Error " + err);
                                    } else {
                                        res.redirect(url.format({
                                            pathname:"/status",
                                            query: {
                                                "submission_status": body.submissionStatus,
                                                "error_status": body.errorStatus
                                            }
                                        }));
                                    }
                                });
                            } else {
                                res.redirect(url.format({
                                    pathname:"/status",
                                    query: {
                                        "submission_status": body.submissionStatus,
                                        "error_status": body.errorStatus
                                    }
                                }));
                            }
                        } else {
                            // Some error in service.
                            res.render('error', {error: "Service Error"});
                        }
                    }
                }
            );
        }
    });
});

module.exports = router;