var express = require('express');
var url = require('url');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');
var async = require('async');

router.get('/', function (req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        var userObj = req.session.user;
        async.parallel([
            function (callable) {
                mongo_helper.GetProblems(function (err, dbResults) {
                    if (err) {
                        callable(err);
                    } else {
                        callable(null, dbResults);
                    }
                });
            }, function (callable) {
                mongo_helper.GetSubmissionsByUserId(userObj.user_id, function (err, dbResults) {
                    if (err) {
                        callable(err);
                    } else {
                        callable(null, dbResults);
                        // callable(dbResults);
                    }
                });
            }
        ], function (err, results) {
            if (err) {
                res.status(500).json("Error loading problems. Error: " + err);
            } else {
                meta_info.problems = [];
                results[0].forEach(function (element) {
                    var solved = false;
                    results[1].forEach(function (submissions) {
                        if (element.problem_id == submissions.problem_id) {
                            solved = true;
                        }
                    });
                    element.solved = solved;
                    meta_info.problems.push(element);
                });
                res.render('dashboard', {meta_data: meta_info});
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

module.exports = router;