package com.rectify.judge.runners;

import com.rectify.judge.utils.SubmissionDetails;
import com.rectify.judge.utils.SubmissionResults;
import com.rectify.judge.utils.SubmissionStatus;
import com.rectify.judge.utils.Testcase;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static com.rectify.judge.utils.Utils.parentDirSubmissions;

/**
 * Created by mohit on 11/10/17.
 */
public class JudgeSubmission implements Callable {

    private SubmissionDetails submissionDetails;
    private static final Logger LOGGER = Logger.getLogger("judge_submission");
    public JudgeSubmission(SubmissionDetails submissionDetails) {
        this.submissionDetails = submissionDetails;
    }

    @Override
    public SubmissionResults call() throws Exception {
        final SubmissionResults submissionResults = new SubmissionResults();
        String email = submissionDetails.email;
        String problemId = submissionDetails.problemId;
        final String code = submissionDetails.code;

        // Write code into a location.
        final String fileName = email + "_" + problemId;
        final String fileNameExt = fileName + ".cc";
        BufferedWriter outputWriter = null;
        try {
            File file = new File(parentDirSubmissions + fileNameExt);
            outputWriter = new BufferedWriter(new FileWriter(file));
            outputWriter.write(code);
            outputWriter.close();
        } catch (Exception e) {
            LOGGER.severe("Could not write to file. Error: " + e);
            submissionResults.setSubmissionStatus(SubmissionStatus.SYSTEM_FAILURE);
            submissionResults.setErrorStatus("Could write to the file. Error: " + e);
            return submissionResults;
        }

        // TODO: Class is not synchronized.
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder("g++", parentDirSubmissions + fileNameExt, "-o", parentDirSubmissions + fileName);
                        Process process = processBuilder.start();
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String line, errorOut = null;
                        while ((line = errorReader.readLine()) != null) {
                            if (errorOut == null)
                                errorOut = line + "\n";
                            else
                                errorOut += line + "\n";
                        }
                        if (errorOut != null) {
                            submissionResults.setSubmissionStatus(SubmissionStatus.COMPILATION_ERROR);
                            submissionResults.setErrorStatus(errorOut);
                        }
                        process.destroy();
                    } catch (Exception e) {
                        submissionResults.setSubmissionStatus(SubmissionStatus.SYSTEM_FAILURE);
                        submissionResults.setErrorStatus("Error while creating binary for the submission.");
                        LOGGER.severe("Error while creating binary for the submission: " + e);
                    }
                }
            };
            Future<?> future = executorService.submit(runnable);
            future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted Exception: " + e);
            submissionResults.setSubmissionStatus(SubmissionStatus.SYSTEM_FAILURE);
            submissionResults.setErrorStatus("SIGSEV");
        } catch (TimeoutException e) {
            submissionResults.setSubmissionStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
            submissionResults.setErrorStatus("TLE");
        } catch (ExecutionException e) {
            submissionResults.setSubmissionStatus(SubmissionStatus.SIGSEV);
            submissionResults.setErrorStatus("SIGSEV");
        }

        if (submissionResults.getSubmissionStatus() != null && submissionResults.getSubmissionStatus() != SubmissionStatus.ACCEPTED) {
            return submissionResults;
        }
        // Run the binary with the test files.
        for (final Testcase testcase : submissionDetails.testcases) {
            final SubmissionResults testSubmissionResult = new SubmissionResults();
            try {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProcessBuilder processBuilder = new ProcessBuilder("./" + fileName);
                            processBuilder.directory(new File(parentDirSubmissions));
                            Process process = processBuilder.start();
                            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                            outputWriter.write(testcase.input_data);
                            outputWriter.flush();
                            outputWriter.close();
                            String line;
                            String codeOutput = "";
                            while ((line = inputReader.readLine()) != null) {
                                codeOutput += line + "\n";
                            }
                            String errorOutput = null;
                            while ((line = errorReader.readLine()) != null) {
                                errorOutput += line + "\n";
                            }
                            errorReader.close();
                            inputReader.close();
                            if (errorOutput != null) {
                                testSubmissionResult.setSubmissionStatus(SubmissionStatus.SIGSEV);
                                testSubmissionResult.setErrorStatus(errorOutput);
                                return;
                            }
                            String correctOutput = testcase.output_data;
                            correctOutput = correctOutput.replaceAll("\\s+", "");
                            codeOutput = codeOutput.replaceAll("\\s+", "");
                            if (!Objects.equals(correctOutput, codeOutput)) {
                                testSubmissionResult.setSubmissionStatus(SubmissionStatus.WRONG_ANSWER);
                                testSubmissionResult.setErrorStatus("Wrong Answer.");
                            } else {
                                testSubmissionResult.setSubmissionStatus(SubmissionStatus.ACCEPTED);
                                testSubmissionResult.setErrorStatus("AC");
                            }
                        } catch (Exception e) {
                            LOGGER.severe("Exception while testing submission. Exception e: " + e);
                        }
                    }
                };

                Future<?> future = executorService.submit(runnable);
                future.get(submissionDetails.timeLimit, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.severe("Interrupted Exception: " + e);
                testSubmissionResult.setSubmissionStatus(SubmissionStatus.SYSTEM_FAILURE);
                testSubmissionResult.setErrorStatus("SIGSEV");
            } catch (TimeoutException e) {
                testSubmissionResult.setSubmissionStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                testSubmissionResult.setErrorStatus("TLE");
            } catch (ExecutionException e) {
                testSubmissionResult.setSubmissionStatus(SubmissionStatus.SIGSEV);
                testSubmissionResult.setErrorStatus("SIGSEV");
            }

            if (testSubmissionResult.getSubmissionStatus() != null && testSubmissionResult.getSubmissionStatus() != SubmissionStatus.ACCEPTED) {
                submissionResults.setSubmissionStatus(testSubmissionResult.getSubmissionStatus());
                submissionResults.setErrorStatus(testSubmissionResult.getErrorStatus());
                return submissionResults;
            }
        }
        submissionResults.setSubmissionStatus(SubmissionStatus.ACCEPTED);
        submissionResults.setErrorStatus("AC");
        return submissionResults;
    }
}
