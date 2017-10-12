package com.rectify.judge.runners;

import com.rectify.judge.utils.HackDetails;
import com.rectify.judge.utils.HackResults;
import com.rectify.judge.utils.HackStatus;

import java.io.*;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static com.rectify.judge.utils.Utils.parentDirHacks;

/**
 * Created by mohit on 13/10/17.
 */
public class JudgeHack implements Callable {

    private static final Logger LOGGER = Logger.getLogger("judge_hack");
    private HackDetails hackDetails;

    public JudgeHack(HackDetails hackDetails) {
        this.hackDetails = hackDetails;
    }

    private HackResults GetHackResults (String code, final String hack_input) {
        final HackResults hackResults = new HackResults();
        // Write correct code into a location.
        Random rand = new Random();
        int n = rand.nextInt(500000) + 1;
        final String fileName = Integer.toString(n);
        final String fileNameExt = fileName + ".cc";
        BufferedWriter outputWriter = null;
        try {
            File file = new File(parentDirHacks + fileNameExt);
            outputWriter = new BufferedWriter(new FileWriter(file));
            outputWriter.write(code);
            outputWriter.close();
        } catch (Exception e) {
            LOGGER.severe("Could not write to file. Error: " + e);
            hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
            hackResults.setErrorStatus("Could write to the file. Error: " + e);
            return hackResults;
        }

        // TODO: Class is not synchronized.
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder("g++", "-w", parentDirHacks + fileNameExt, "-o", parentDirHacks + fileName);
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
                            hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
                            hackResults.setErrorStatus(errorOut);
                        }
                        process.destroy();
                    } catch (Exception e) {
                        hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
                        hackResults.setErrorStatus("Error while creating binary for the submission.");
                        LOGGER.severe("Error while creating binary for the submission: " + e);
                    }
                }
            };
            Future<?> future = executorService.submit(runnable);
            future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOGGER.severe("Interrupted Exception: " + e);
            hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
            hackResults.setErrorStatus("SIGSEV");
        }

        // Check if any error has occured.
        if (hackResults.getHackStatus() != null && hackResults.getHackStatus() != HackStatus.SUCCESSFUL) {
            return hackResults;
        }
        final String[] codeOutput = {null};
        // Run the binary with the test files.
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder("./" + fileName);
                        processBuilder.directory(new File(parentDirHacks));
                        Process process = processBuilder.start();
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                        outputWriter.write(hack_input);
                        outputWriter.flush();
                        outputWriter.close();
                        String line;
                        while ((line = inputReader.readLine()) != null) {
                            codeOutput[0] += line + "\n";
                        }
                        String errorOutput = null;
                        while ((line = errorReader.readLine()) != null) {
                            errorOutput += line + "\n";
                        }
                        errorReader.close();
                        inputReader.close();
                        if (errorOutput != null) {
                            hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
                            hackResults.setErrorStatus(errorOutput);
                            return;
                        }
                    } catch (Exception e) {
                        LOGGER.severe("Exception while testing submission. Exception e: " + e);
                    }
                }
            };
            Future<?> future = executorService.submit(runnable);
            future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOGGER.severe("Interrupted Exception: " + e);
            hackResults.setHackStatus(HackStatus.UNSUCCESSFUL);
            hackResults.setErrorStatus("SIGSEV");
        }

        if (hackResults.getHackStatus() == null) {
            hackResults.setHackStatus(HackStatus.SUCCESSFUL);
            hackResults.setCodeOutput(codeOutput[0]);
            hackResults.setErrorStatus("");
        }
        return hackResults;
    }

    @Override
    public Object call() throws Exception {
        String correct_code = hackDetails.correct_code;
        String submitted_code = hackDetails.submitted_code;
        final String hack_input = hackDetails.hack_input;

        HackResults hackResults1 = GetHackResults(correct_code, hack_input);
        HackResults hackResults2 = GetHackResults(submitted_code, hack_input);

        if (hackResults1.getHackStatus() == HackStatus.SUCCESSFUL && hackResults2.getHackStatus() == HackStatus.SUCCESSFUL) {
            if (!Objects.equals(hackResults1.getCodeOutput(), "") && !Objects.equals(hackResults2.getCodeOutput(), "") && !Objects.equals(hackResults1.getCodeOutput(), hackResults2.getCodeOutput()))
                return new HackResults(HackStatus.SUCCESSFUL);
            else
                return new HackResults(HackStatus.UNSUCCESSFUL);
        } else {
            return new HackResults(HackStatus.UNSUCCESSFUL);
        }
    }
}
