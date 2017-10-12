var express = require('express');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');
const url = require('url');
var meta_info = require('../bin/meta');

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta = meta_info.getMeta();
    if (req.session.user) {
        res.redirect(url.format({
            pathname: "/dashboard"
        }));
    } else {
        meta.error_message = req.query.err_message;
        res.render('login', {meta_data: meta});
    }

});

/* Submit Login.*/
router.post('/', function (req, res, next) {
    var user_id = req.body.user_id;
    var password = req.body.password;
    mongo_helper.FindUser(user_id, function (err, dbResult) {
        if (err) {
            req.session.user = null;
            res.redirect(url.format({
                pathname:"/login",
                query: {
                    "err_message": err
                }
            }));
        } else {
            if (dbResult.length == 1 && dbResult[0].password == password) {
                var user = {};
                user.user_id = user_id;
                user.first_name = dbResult[0].first_name;
                user.last_name = dbResult[0].last_name;
                req.session.user = user;
                res.redirect('/dashboard')
            } else {
                req.session.user = null;
                res.redirect(url.format({
                    pathname:"/login",
                    query: {
                        "err_message": "Wrong Password."
                    }
                }));
            }
        }
    });
});

module.exports = router;
