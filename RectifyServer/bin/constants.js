var cons = {};
var CryptoJS = require("crypto-js");
// Database server information.
cons.port = 27017;
cons.host = "localhost";
cons.dbTest = "RectifyTest";
cons.dbProd = "Rectify";

// Collection names.
cons.UsersColl = "Users";
cons.ProblemsColl = "Problems";
cons.SubmissionsColl = "Submissions";
cons.TestcasesColl = "Testcases";
cons.HacksColl = "Hacks";
cons.SystemTestsColl = "SystemTests";

// Authentication.
cons.auth = "";//"mohitreddy1996:mohitreddy1996";

// Phases.
cons.CHILL_PHASE = "CHILL_PHASE";
cons.CODING_PHASE = "CODING_PHASE";
cons.HACKING_PHASE = "HACKING_PHASE";
cons.SYSTEM_TESTING_PHASE = "SYSTEM_TESTING_PHASE";

// Database to use
cons.dbUrl = cons.dbTest;
// Set a specific time for the contest
cons.StartTime = new Date().getTime();
// Ending time for phases
cons.CHILL_PHASE_DUR = 0;
cons.CODING_PHASE_DUR = 1;
cons.HACKING_PHASE_DUR = 1;
cons.SYSTEM_TESTING_PHASE_DUR = 5;

// Multiplier.
cons.MULTIPLIER = 60*1000;

// KEY
cons.KEY = "RECTIFY";

cons.getDBUrl = function () {
    return "mongodb://" + cons.auth + "@" + cons.host + ":" + cons.port + "/" + cons.dbUrl;
};



// Encrypt 
cons.encrypt = function (message) {
    var ciphertext = CryptoJS.AES.encrypt(message, cons.KEY);
    return ciphertext.toString();
};

cons.decrypt = function (message) {
    // Decrypt 
    var bytes  = CryptoJS.AES.decrypt(message, cons.KEY);
    return bytes.toString(CryptoJS.enc.Utf8);
};

cons.SampleTestScore = 25;
cons.SystemTestScore = 100;
cons.SuccessfullHackScore = 50;
cons.UnsuccessfullHackScore = -25;

module.exports = cons;