var express = require('express');
var router = express.Router();
var meta = require('../bin/meta');

/* GET home page. */
router.get('/', function(req, res, next) {
    var meta_info = meta.getMeta();
    if (req.session.user) {
        meta_info.is_logged_in = true;
        res.render('index', {meta_data: meta_info});
    } else {
      res.render('index', {meta_data: meta_info});
    }
});

module.exports = router;
