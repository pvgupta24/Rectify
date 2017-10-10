var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
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