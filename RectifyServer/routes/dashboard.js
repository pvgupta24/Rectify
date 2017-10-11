var express = require('express');
var url = require('url');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');

router.get('/', function (req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        if (meta_info.problems_info == null) {
            meta_info.problems_info = {};
        }
        mongo_helper.GetProblems(function (err, dbResults) {
            if (err) {
                meta_info.problems_info.error = true;
            } else {
                meta_info.problems_info.error = false;
                meta_info.problems_info.problems = [];
                dbResults.forEach(function (problem_info) {
                     meta_info.problems_info.problems.push(problem_info);
                });
            }
        });
        res.render('dashboard', {meta_data: meta_info});
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