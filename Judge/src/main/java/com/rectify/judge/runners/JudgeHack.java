package com.rectify.judge.runners;

import static com.rectify.judge.utils.Utils.parentDirHacks;

import java.io.*;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Logger;

import com.rectify.judge.helper.Runner;
import com.rectify.judge.utils.HackDetails;
import com.rectify.judge.utils.HackResults;
import com.rectify.judge.utils.HackStatus;
import com.rectify.judge.utils.SubmissionResults;
import com.rectify.judge.utils.Testcase;

/**
 * Compiles and runs the target submission in a thread and returns the
 * {@link HackResults} by running against a custom test case and comparing with
 * results of the solution.
 * 
 * @author Mohit Reddy
 */
public class JudgeHack implements Callable {

    private static final Logger LOGGER
        = Logger.getLogger(JudgeHack.class.getName());
    private HackDetails hackDetails;

    public JudgeHack(HackDetails hackDetails) {
        this.hackDetails = hackDetails;
    }

    private HackResults GetHackResults(String code, final Testcase testcase) {
        final HackResults hackResults = new HackResults();
        // Write correct code into a location.
        Random rand = new Random();
        int n = rand.nextInt(500000) + 1;
        final String fileName = Integer.toString(n);
        final String fileNameExt = fileName + ".cc";
        try {
            File file = new File(parentDirHacks + fileNameExt);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(file));
            outputWriter.write(code);
            outputWriter.close();
        } catch (Exception e) {
            LOGGER.severe("Could not write to file. Error: " + e);
            hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
            hackResults.setErrorStatus("Could write to the file. Error: " + e);
            return hackResults;
        }

        // TODO: Class is not synchronized.
        // TODO: Correct solution should not be compiled for every Hack submission
        SubmissionResults result = Runner.compileCode(fileName, fileNameExt);
        
        // FIXME : Hacks should not be unsuccessfull if correct solution fails
        // to compile
        if(result.getErrorStatus() != null)
            return new HackResults(null, result);

        // FIXME : Use problem's time constraint
        result = Runner.getCodeOutput(testcase, fileNameExt, 10);
        
        return new HackResults(HackStatus.SUCCESSFUL, result);
    }

    @Override
    public Object call() throws Exception {
        String correct_code = hackDetails.correct_code;
        String submitted_code = hackDetails.submitted_code;
        final String hack_input = hackDetails.hack_input;

        HackResults correctHackResults
            = GetHackResults(correct_code, new Testcase(hack_input, null));

        if(correctHackResults.getHackStatus() == null)
            return new HackResults(null);

        HackResults submittedHackResults
            = GetHackResults(submitted_code,
                new Testcase(hack_input, correctHackResults.getCodeOutput()));
        
        return submittedHackResults;
        // if (submittedHackResults.getHackStatus() == HackStatus.SUCCESSFUL) {
        //         return new HackResults(HackStatus.SUCCESSFUL);
        // } else {
        //     return new HackResults(HackStatus.UNSUCCESSFUL);
        // }
    }
}
