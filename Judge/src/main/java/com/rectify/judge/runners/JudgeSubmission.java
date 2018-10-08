package com.rectify.judge.runners;

import static com.rectify.judge.utils.Utils.parentDirSubmissions;

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

import com.rectify.judge.helper.Runner;
import com.rectify.judge.utils.SubmissionDetails;
import com.rectify.judge.utils.SubmissionResults;
import com.rectify.judge.utils.SubmissionStatus;
import com.rectify.judge.utils.Testcase;

/**
 * Compiles and runs the submitted code in a thread and returns
 * {@link SubmissionResults} by saving as a separate file.
 * 
 * @author Mohit Reddy
 */
public class JudgeSubmission implements Callable {

    private SubmissionDetails submissionDetails;
    private static final Logger LOGGER = Logger.getLogger("judge_submission");
    private static final String FILE_DELIMITER = "_";

    // TODO : Make the implementation abstract to support multiple languages.
    private static final String languageExtension = ".cc";

    public JudgeSubmission(SubmissionDetails submissionDetails) {
        this.submissionDetails = submissionDetails;
    }

    @Override
    public SubmissionResults call() throws Exception {
        SubmissionResults submissionResults = new SubmissionResults();
        String userId = submissionDetails.userId;
        String problemId = submissionDetails.problemId;
        final String code = submissionDetails.code;

        // Write submitted code to a file
        final String fileName = userId + FILE_DELIMITER + problemId;
        final String fileNameExt = fileName + languageExtension;
        // BufferedWriter outputWriter = null;
        try {
            File file = new File(parentDirSubmissions + fileNameExt);
            BufferedWriter outputWriter
                = new BufferedWriter(new FileWriter(file));
            outputWriter.write(code);
            outputWriter.close();
        } catch (Exception e) {
            LOGGER.severe("Error saving submission to file :" + e);
            submissionResults.setSubmissionStatus(
                SubmissionStatus.SYSTEM_FAILURE);
            submissionResults.setErrorStatus(
                "Error saving submission to file :" + e);

            return submissionResults;
        }
        // TODO: Class is not synchronized.
        // ExecutorService executorService = Executors.newSingleThreadExecutor();

        submissionResults = Runner.compileCode(fileName, fileNameExt);
        if(submissionResults.getSubmissionStatus() != null)
        {
            return submissionResults;
        }
        LOGGER.info(submissionDetails.testcases.toString());
        // Run the binary with the test files.
        for (final Testcase testcase : submissionDetails.testcases) {
            LOGGER.info(testcase.input_data);
            LOGGER.info(testcase.output_data);

            final SubmissionResults testSubmissionResult
                = Runner.getCodeOutput(testcase, fileName,
                    submissionDetails.timeLimit);
            
            // Set submission status if test fails for the test case
            if (testSubmissionResult.getSubmissionStatus() != null
                    && testSubmissionResult.getSubmissionStatus()
                            != SubmissionStatus.ACCEPTED) {
                submissionResults.setSubmissionStatus(
                    testSubmissionResult.getSubmissionStatus());
                submissionResults.setErrorStatus(
                    testSubmissionResult.getErrorStatus());

                return submissionResults;
            }
        }
        submissionResults.setSubmissionStatus(SubmissionStatus.ACCEPTED);
        submissionResults.setErrorStatus("AC");

        return submissionResults;
    }
}
