package com.rectify.judge.helper;

import static com.rectify.judge.utils.Utils.parentDirSubmissions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.rectify.judge.utils.SubmissionResults;
import com.rectify.judge.utils.SubmissionStatus;
import com.rectify.judge.utils.Testcase;

/**
 * 
 * @author Praveen Gupta
 */
public class Runner {
    private static final Logger LOGGER
        = Logger.getLogger(Runner.class.getName());

    // Compilation time limit in seconds.
    private static final int COMPILATION_TIME_LIMIT = 5;

    /**
     * Compiles the given file and returns the compilation status
     * 
     * @param fileName to compile and make the binary
     * @param fileNameExt the filename with extension
     * @return {@link SubmissionResults} denoting the compilation status
     */
    public static SubmissionResults compileCode(String fileName,
                                                String fileNameExt,
                                                String parentDir){
        // TODO: Class is not synchronized.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        final SubmissionResults submissionResults = new SubmissionResults();
        try {
            Runnable runnable = () -> {
                try {
                // Compiles the submission by starting a new process.
                    ProcessBuilder processBuilder
                        = new ProcessBuilder(
                            "g++",
                            "-w",
                            parentDir + fileNameExt,    
                            "-o",
                            parentDir + fileName
                        );
                    Process process = processBuilder.start();
                    BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                    String errorOut = errorReader.lines().collect(
                        Collectors.joining("\n"));
                    if (errorOut != null && !errorOut.isEmpty()) {
                        submissionResults.setSubmissionStatus(
                            SubmissionStatus.COMPILATION_ERROR);
                            submissionResults.setErrorStatus(errorOut);
                        LOGGER.info(errorOut);

                        return;
                    }
                    process.destroy();
                } catch (Exception e) {
                    submissionResults.setSubmissionStatus(
                        SubmissionStatus.SYSTEM_FAILURE);
                    submissionResults.setErrorStatus(
                        "Error while compiling the solution.");
                    LOGGER.severe("Error while compiling the solution : " + e);
                }
            };
            Future<?> future = executorService.submit(runnable);
            future.get(COMPILATION_TIME_LIMIT , TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted Exception: " + e);
            submissionResults.setSubmissionStatus(
                SubmissionStatus.SYSTEM_FAILURE);
            submissionResults.setErrorStatus("SIGSEV");
        } catch (TimeoutException e) {
            submissionResults.setSubmissionStatus(
                SubmissionStatus.TIME_LIMIT_EXCEEDED);
            submissionResults.setErrorStatus("TLE");
        } catch (ExecutionException e) {
            submissionResults.setSubmissionStatus(SubmissionStatus.SIGSEV);
            submissionResults.setErrorStatus("SIGSEV");
        }

        return submissionResults;
    }

    /**
     * Runs a binary with the given test case and checks with the expected output
     * of the test case.
     * 
     * @param testcase to run the binary against
     * @param fileName the binary file to execute
     * @param timeLimit the time constraint for the test case
     * @return
     */
    public static SubmissionResults getCodeOutput(Testcase testcase,
                            String fileName, String parentDir, int timeLimit){
        final SubmissionResults testSubmissionResult = new SubmissionResults();
        final ExecutorService executorService
            = Executors.newSingleThreadExecutor();
        try {
            Runnable runnable = () -> {
                try {
                    ProcessBuilder processBuilder
                        = new ProcessBuilder("./" + fileName);
                    processBuilder.directory(
                        new File(parentDir));
                    Process process = processBuilder.start();
                    BufferedReader inputReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                    BufferedWriter outputWriter = new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream()));

                    outputWriter.write(testcase.input_data);
                    outputWriter.flush();
                    outputWriter.close();

                    String codeOutput = inputReader.lines().collect(
                        Collectors.joining("\n"));
                    String errorOutput = errorReader.lines().collect(
                        Collectors.joining("\n"));

                    errorReader.close();
                    inputReader.close();

                    if (errorOutput != null && !errorOutput.isEmpty()) {
                        testSubmissionResult.setSubmissionStatus(
                        SubmissionStatus.SIGSEV);
                        testSubmissionResult.setErrorStatus(errorOutput);
                        LOGGER.severe(errorOutput);

                        return;
                    }
                    String correctOutput = testcase.output_data;
                    correctOutput = correctOutput.replaceAll("\\s+", "");
                    codeOutput = codeOutput.replaceAll("\\s+", "");
                    testSubmissionResult.setCodeOutput(codeOutput);
                    if (!correctOutput.equals(codeOutput)) {
                        testSubmissionResult.setSubmissionStatus(
                            SubmissionStatus.WRONG_ANSWER);
                        testSubmissionResult.setErrorStatus("Wrong Answer");
                    } else {
                        testSubmissionResult.setSubmissionStatus(
                        SubmissionStatus.ACCEPTED);
                        testSubmissionResult.setErrorStatus("AC");
                    }
                } catch (Exception e) {
                    LOGGER.severe(
                        "Exception while testing submission : " + e);
                        e.printStackTrace();
                }
            };

            Future<?> future = executorService.submit(runnable);
            future.get(timeLimit, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted Exception: " + e);
            testSubmissionResult.setSubmissionStatus(
                SubmissionStatus.SYSTEM_FAILURE);
            testSubmissionResult.setErrorStatus("SIGSEV");
        } catch (TimeoutException e) {
            testSubmissionResult.setSubmissionStatus(
                SubmissionStatus.TIME_LIMIT_EXCEEDED);
            testSubmissionResult.setErrorStatus("TLE");
        } catch (ExecutionException e) {
            testSubmissionResult.setSubmissionStatus(
                SubmissionStatus.SIGSEV);
            testSubmissionResult.setErrorStatus("SIGSEV");
        }
        
        return testSubmissionResult;
    }
}
