var express = require('express');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');
var url = require("url");

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        var userObj = req.session.user;
        meta_info.is_logged_in = true;
        mongo_helper.GetAllSubmissions(function (err, result) {
            if (err) {
                res.status(500).json("Failed to fetch submissions. Error: " + err);
            } else {
                meta_info.hacks = [];
                result.forEach(function (element) {
                    if (element.user_id != userObj.user_id) {
                        element.hacked = false;
                        meta_info.hacks.push(element);
                    }
                });
                mongo_helper.GetHacksByUser(userObj.user_id, function (err, dbresult) {
                     dbresult.forEach(function (hackElem) {
                         meta_info.hacks.forEach(function (hackableElem) {
                             if (hackableElem.user_id == hackElem.opponent_id && 
                                 hackableElem.problem_id == hackElem.problem_id) {
                                 hackableElem.hacked = true;
                             }
                         });
                     });
                     res.render('hack', {meta_data: meta_info});
                });
            }
        });

    } else {
        meta_info.is_logged_in = false;
        res.redirect(url.format({
            pathname:"/login",
            query: {
                "err_message": "Wrong Password."
            }
        }));
    }
});

module.exports = router;
