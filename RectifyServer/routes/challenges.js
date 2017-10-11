var express = require('express');
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
    var problem_id = req.query.problem_id;
    var userObj = req.session.user;
    var submission = req.body.code;
    // Add timestamp.
});

module.exports = router;