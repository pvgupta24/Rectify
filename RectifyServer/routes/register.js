var express = require('express');
var router = express.Router();
var meta = require('../bin/meta');

var mongo_helper = require('../bin/mongoHelper');
/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        res.render('register', {meta_data: meta_info});
    } else {
        meta_info.is_logged_in = false;
        res.render('register', {meta_data: meta_info});
    }
});

/* Submit Register. */
router.post('/', function (req, res, next) {
    var first_name = req.body.first_name;
    var last_name = req.body.last_name;
    var email = req.body.email;
    var password = req.body.password;
    var user_id = req.body.user_id;
    var meta_info = meta.getMeta();
    mongo_helper.FindUser(user_id, function (err, dbResults) {
        if (err) {
            meta_info.is_logged_in = false;
            res.status(500).json("Error while looking for user.");
        } else {
            if (dbResults.length > 0) {
                meta_info.is_logged_in = false;
                res.redirect(url.format({
                    pathname:"/login",
                    query: {
                        "err_message": "User already taken."
                    }
                }));
            } else {
                mongo_helper.AddUser(first_name, last_name, email, password, user_id, function (err, dbRes) {
                    if (err) {
                        meta_info.is_logged_in = false;
                        res.status(500).json("Error while registering you. Error: ", err);
                    } else {
                        var user = {};
                        user.user_id = user_id;
                        user.first_name = first_name;
                        user.last_name = last_name;
                        req.session.user = user;
                        meta_info.is_logged_in = true;
                        res.redirect('/');
                    }
                });
            }
        }
    });

});

module.exports = router;
