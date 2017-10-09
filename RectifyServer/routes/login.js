var express = require('express');
var router = express.Router();
var mongo_helper = require('../bin/mongoHelper');

/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('login');
});

/* Submit Login.*/
router.post('/submit', function (req, res, next) {
    var email = req.body.email;
    var password = req.body.password;
    mongo_helper.FindUser(email, function (err, dbResult) {
        if (err) {
            res.status(500).json("Error while logging in user. Error: ", err);
        } else {
            if (dbResult.length > 0 && dbResult[0].password == password) {
                res.status(200).json("User login successful");
            } else {
                res.status(304).json("User not registered or password not write.");
            }
        }
    });
});

module.exports = router;
