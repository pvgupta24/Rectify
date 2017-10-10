var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  if (req.session.user) {
      res.render('index', {is_logged_in: true});
  } else {
      res.render('index');
  }
});

module.exports = router;
