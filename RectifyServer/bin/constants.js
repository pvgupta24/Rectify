var cons = {};

// Database server information.
cons.port = 27017;
cons.host = "localhost";
cons.dbTest = "RectifyTest";
cons.dbProd = "Rectify";

// Collection names.
cons.UsersColl = "Users";
cons.ProblemsColl = "Problems";
cons.SubmissionsColl = "Submissions";

// Authentication.
cons.auth = "mohitreddy1996:mohitreddy1996";

cons.getDBUrl = function (dbName) {
    if (dbName == cons.dbProd) {
        return "mongodb://" + cons.auth + "@" + cons.host + ":" + cons.port + "/" + dbName;
    }  else if (dbName == cons.dbTest) {
        return "mongodb://" + cons.auth + "@" + cons.host + ":" + cons.port + "/" + dbName
    } else {
        console.log("Wrong database.");
        process.exit(1);
    }
};

module.exports = cons;