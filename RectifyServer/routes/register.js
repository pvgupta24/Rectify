var express = require('express');
var router = express.Router();

var mongo_helper = require('../bin/mongoHelper');
/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('register');
});

/* Submit Register. */
router.post('/', function (req, res, next) {
    var first_name = req.body.first_name;
    var last_name = req.body.last_name;
    var email = req.body.email;
    var password = req.body.password;
    mongo_helper.AddUser(first_name, last_name, email, password, function (err, dbRes) {
        if (err) {
            res.status(500).json("Error while registering you. Error: ", err);
        } else {
            res.status(200).json("Successfully registered.");
        }
    });
});

module.exports = router;
