var express = require('express');
var router = express.Router();
var meta = require('../bin/meta');

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    meta_info.is_logged_in = false;
    if (req.session.user) {
        req.session.destroy(function(){
            console.log("user logged out.")
        });
        res.redirect('/');
    } else {
        res.redirect('/');
    }
});

module.exports = router;