var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');

var index = require('./routes/index');
var login = require('./routes/login');
var register = require('./routes/register');
var mongo_client = require('./bin/mongoClient');
var cons = require('./bin/constants');
var dashboard = require('./routes/dashboard');
var logout = require('./routes/logout');
var challenges = require('./routes/challenges');
var status = require('./routes/status');
var leaderboard = require('./routes/leaderboard');
var hack = require('./routes/hack');
var hack_solution = require('./routes/hack_solution');
var meta = require('./bin/meta');
var system_testing = require('./routes/system_testing');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'views')));
app.use(session({
    secret: 'mohitreddy1996',
    saveUninitialized: false,
    resave: false
}));

app.use('/', index);
app.use('/login', login);
app.use('/register', register);
app.use('/dashboard', dashboard);
app.use('/logout', logout);
app.use('/challenge', challenges);
app.use('/status', status);
app.use('/leaderboard', leaderboard);
app.use('/hack', hack);
app.use('/hack_solution', hack_solution);
app.use('/system_testing', system_testing);


// connect to the mongo.
var dbUrl = null;
meta.initialiseMeta();
/*if (process.env.test) {
    dbUrl = cons.getDBUrl(cons.dbTest);
} else {
    dbUrl = cons.getDBUrl(cons.dbProd);
}*/
dbUrl = cons.getDBUrl(cons.dbTest);

mongo_client.connect(dbUrl, function (err) {
    if (err) {
        console.log("Error while connecting to the database ", err);
        process.exit(1);
    } else {
        console.log("Connected to database on port: ", cons.port);
    }
});

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
