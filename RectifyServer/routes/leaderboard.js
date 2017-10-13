var express = require('express');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
var meta = require('../bin/meta');
var url = require("url");

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        mongo_helper.GetLeaderboard(function (err, dbResults) {
            if (err) {
                res.status(500).json("Error making the leaderboard");
            } else {
                meta_info.leaderboard = [];
                var rank = 1;
                dbResults.forEach(function (element) {
                    var contestantObj = {};
                    contestantObj.rank = rank;
                    contestantObj.userId = element.user_id;
                    contestantObj.score = element.score;
                    contestantObj.time = element.time
                    rank += 1;
                    meta_info.leaderboard.push(contestantObj);
                });
                res.render('leaderboard', {meta_data: meta_info});
            }
        });
        
    } else {
        meta_info.is_logged_in = false;
        res.redirect(res.redirect(url.format({
            pathname:"/login",
            query: {
                "err_message": "Wrong Password."
            }
        })));
    }
});

module.exports = router;
