var express = require('express');
var url = require('url');
var router = express.Router();

router.get('/', function (req, res, next) {
    if (req.session.user) {
        res.render('dashboard', {is_logged_in: true});
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