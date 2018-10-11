package com.rectify.judge.runners;

import static com.rectify.judge.utils.Utils.parentDirHacks;
import static com.rectify.judge.utils.Utils.parentDirSolutions;

import java.io.*;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Logger;

import com.rectify.judge.helper.Runner;
import com.rectify.judge.utils.HackDetails;
import com.rectify.judge.utils.HackResults;
import com.rectify.judge.utils.HackStatus;
import com.rectify.judge.utils.SubmissionResults;
import com.rectify.judge.utils.SubmissionStatus;
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

    @Override
    public Object call() throws Exception {
        String correct_code = hackDetails.correct_code;
        String submitted_code = hackDetails.submitted_code;
        String opponent_id = hackDetails.opponent_id;
        String problemId = hackDetails.problem_id;
        String userId = hackDetails.user_id;
        final String hack_input = hackDetails.hack_input;

        // Write correct code to a file
        String fileName = problemId;
        String fileNameExt = fileName + ".cc";

        HackResults hackResults = new HackResults();
        SubmissionResults submissionResults
            = Runner.compileCode(fileName, fileNameExt, parentDirSolutions);
        if(submissionResults.getSubmissionStatus() != null)
        {
            return new HackResults(HackStatus.SYSTEM_ERROR, submissionResults);
        }
        submissionResults = Runner.getCodeOutput(
                new Testcase(hack_input, ""), fileName, parentDirSolutions, 10);
        if (submissionResults.getSubmissionStatus()
                != SubmissionStatus.WRONG_ANSWER)
        {
            return new HackResults(HackStatus.SYSTEM_ERROR, submissionResults);
        }
        LOGGER.info(submissionResults.getErrorStatus());
        LOGGER.info("Expected Output : " + submissionResults.getCodeOutput());
        final String correctOutput = submissionResults.getCodeOutput();
        fileName = userId + "_hack_" + opponent_id + "_on_" + problemId;
        fileNameExt = fileName + ".cc";
        try {
            File file = new File(parentDirHacks + fileNameExt);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(file));
            outputWriter.write(submitted_code);
            outputWriter.close();
        } catch (Exception e) {
            LOGGER.severe("Could not write to file. Error: " + e);
            hackResults.setHackStatus(HackStatus.SYSTEM_ERROR);
            hackResults.setErrorStatus("Could write to the file. Error: " + e);
            return hackResults;
        }

        // TODO: Class is not synchronized.
        submissionResults
            = Runner.compileCode(fileName, fileNameExt, parentDirHacks);
        if(submissionResults.getErrorStatus() != null){
            LOGGER.severe("Error : " + submissionResults.getErrorStatus());
            return new HackResults(HackStatus.SYSTEM_ERROR, submissionResults);
        }
        // FIXME : Use problem's time constraint
        submissionResults = Runner.getCodeOutput(
            new Testcase(hack_input, correctOutput),
            fileName, parentDirHacks, 10);
        // LOGGER.info("Custom Output is : \n" + submissionResults.getCodeOutput());
        if(submissionResults.getErrorStatus() == null){
            LOGGER.severe("Error : \n" + submissionResults.getSubmissionStatus());
            return new HackResults(HackStatus.SYSTEM_ERROR, submissionResults);
        }

        LOGGER.info("Custom Output is : \n" + submissionResults.getCodeOutput());
        if(submissionResults.getSubmissionStatus() == SubmissionStatus.ACCEPTED)
            return new HackResults(HackStatus.UNSUCCESSFUL, submissionResults);
        else if(submissionResults.getSubmissionStatus() == SubmissionStatus.WRONG_ANSWER)
            return new HackResults(HackStatus.SUCCESSFUL, submissionResults);
        else
            return new HackResults(HackStatus.SYSTEM_ERROR, submissionResults);
    }
}
