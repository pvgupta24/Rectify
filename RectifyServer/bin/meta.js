var cons = require("./constants.js");
var meta = {};

var meta_info = {
    contest_start_time: null,
    phase: null
};

meta.getMeta = function () {
    meta.loadMeta();
    return meta_info;
};

meta.initialiseMeta = function () {
    meta_info.contest_start_time = new Date().getTime();
    meta_info.phase = cons.CHILL_PHASE;
};

meta.loadMeta = function () {
    var curr_time = new Date().getTime();
    // load meta data.
    if (meta_info.contest_start_time + cons.CHILL_PHASE_DUR * cons.MULTIPLIER > curr_time) {
        meta_info.phase = cons.CHILL_PHASE;
    } else if (meta_info.contest_start_time + cons.CODING_PHASE_DUR * cons.MULTIPLIER > curr_time) {
        meta_info.phase = cons.CODING_PHASE;
    } else if (meta_info.contest_start_time + cons.HACKING_PHASE_DUR * cons.MULTIPLIER > curr_time) {
        meta_info.phase = cons.HACKING_PHASE;
    } else if (meta_info.contest_start_time + cons.SYSTEM_TESTING_PHASE * cons.MULTIPLIER > curr_time) {
        meta_info.phase = cons.SYSTEM_TESTING_PHASE;
    }
};

module.exports = meta;