var express = require('express');
var request = require('request');
var url = require('url');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        var problem_id = req.query.problem_id;
        mongo_helper.GetProblemById(problem_id, function (err, dbResults) {
            if (err) {
                meta_info.problems_info.error = true;
            } else {
                if (dbResults.length == 1) {
                    meta_info.problems_info.error = false;
                    meta_info.problems_info.problems = [];
                    dbResults.forEach(function (problem_info) {
                        meta_info.problems_info.problems.push(problem_info);
                    });
                } else {
                    meta_info.problems_info.error = true;
                }
            }
        });
        res.render('challenge', {meta_data: meta_info});
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
    submissionObj.email = userObj.email;
    submissionObj.code = submission;
    submissionObj.problemId = problem_id;
    submissionObj.testcases = [];

    mongo_helper.GetTestcases(problem_id, function (err, dbResults) {
        if (err) {
            console.log("Error!", err);
            res.status(500).json("Error while fetching test cases.");
        } else {
            dbResults.forEach(function(element) {
                var testObj = {};
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
                        if (response.status == 200) {
                            // Successful.
                            res.render('', {})
                        } else {
                            // Some error in service.
                        }
                    }
                }
            );
        }
    });
});

module.exports = router;