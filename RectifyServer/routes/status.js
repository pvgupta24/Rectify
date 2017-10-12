var express = require('express');
var router = express.Router();
var meta = require('../bin/meta');
var url = require("url");

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        meta_info.submission_status = req.query.submission_status;
        meta_info.error_status = req.query.error_status;
        res.render('status', {meta_data: meta_info});
    } else {
        res.redirect(url.format({
            pathname:"/login",
            query: {
                "err_message": "Login please."
            }
        }));
    }
});

module.exports = router;