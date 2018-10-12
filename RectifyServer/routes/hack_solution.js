var express = require('express');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');
var url = require("url");
var request = require('request');
var async = require('async');
var cons = require('../bin/constants');

router.get('/', function (req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        var opponentId = req.query.user_id;
        var problemId = req.query.problem_id;
        mongo_helper.GetSubmissionOfUser(opponentId, problemId, function (err, result) {
            if (err) {
                res.status(500).json("Error getting opponents solution. Error : " + err);
            } else {
                if (result.length > 0) {
                    // var code = "<p>";
                    // code += result[0].code;
                    // code = code.replace(/(?:\r\n|\r|\n)/g, '<br />');
                    // code += "</p>";
                    meta_info.opponent_code = result[0].code;
                    res.render('hack_solution', {meta_data: meta_info});
                } else {
                    res.status(500).json("Error, no submission made by the user.");
                }
            }
        });
    } else {
        res.redirect(url.format({
            pathname:"/login",
            query: {
                "err_message": "Wrong Password."
            }
        }));
    }
});

router.post('/', function (req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        var userObj = req.session.user;
        meta_info.is_logged_in = true;
        var opponentId = req.query.user_id;
        var problemId = req.query.problem_id;
        var code = req.body.code;
        async.parallel([
            function (callable) {
                mongo_helper.GetSubmissionOfUser(opponentId, problemId, function (err, result) {
                    if (err) {
                        callable(err);
                    } else {
                        callable(null, result);
                    }
                });
            } , function (callable) {
                mongo_helper.GetProblemById(problemId, function (err, result) {
                    if (err) {
                        callable(err);
                    } else {
                        callable(null, result);
                    }
                });
            }
        ], function (err, result) {
            if (err) {
                res.status(500).json("Error while fetching information for hack! Error: " + err);
            } else {
                var correct_code = result[1][0].correct_code;
                var submitted_code = result[0][0].code;
                var hack_input = code;
                var hackObj = {};
                hackObj.correct_code = correct_code;
                hackObj.hack_input = hack_input;
                hackObj.submitted_code = submitted_code;
                hackObj.opponent_id = opponentId;
                hackObj.problem_id = problemId;
                hackObj.user_id = userObj.user_id;
                request.post('http://0.0.0.0:9002/hack/submit',
                    {json: hackObj},
                    function (error, response, body) {
                        if (error) {
                            console.log("Error!");
                            res.status(500).json("Error service.");
                        } else {
                            if (response.statusCode == 200) {
                                // Successful. If successful update the score and store the submission for hacking.
                                var score = 0;
                                var addHack = false;
                                if (body.hackStatus == "SUCCESSFUL") {
                                    score = cons.SuccessfullHackScore;
                                    addHack = true;
                                } else if (body.hackStatus == "UNSUCCESSFUL") {
                                    score = cons.UnsuccessfullHackScore;
                                }
                                async.parallel([
                                    function (callable) {
                                        if (addHack) {
                                            mongo_helper.AddHacks(userObj.user_id, problemId, opponentId, function (err, result) {
                                                if (err) {
                                                    callable(err);
                                                } else {
                                                    callable(null, result);
                                                }
                                            });
                                        } else {
                                            callable(null);
                                        }
                                    } , function (callable) {
                                        mongo_helper.UpdateScore(userObj.user_id, score, 0, function (err, result) {
                                            if (err) {
                                                callable(err);
                                            } else {
                                                callable(null, result);
                                            }
                                        })
                                    }
                                ], function (err, result) {
                                    if (err) {
                                        res.status(500).json("Error saving your submission to the database. Error " + err);
                                    } else {
                                        res.redirect(url.format({
                                            pathname: "/status",
                                            query: {
                                                "submission_status": body.hackStatus,
                                                "error_status": body.errorStatus
                                            }
                                        }));
                                    }
                                });
                            } else {
                                // Some error in service.
                                res.render('error', {error: "Service Error"});
                            }
                        }
                    }
                )
            }
        });
    } else {
        res.redirect(url.format({
            pathname:"/login",
            query: {
                "err_message": "Wrong Password."
            }
        }));
    }
});

module.exports = router;
