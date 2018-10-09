package com.rectify.judge.server;

import java.io.Serializable;
import java.util.concurrent.*;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.rectify.judge.runners.JudgeHack;
import com.rectify.judge.utils.HackDetails;
import com.rectify.judge.utils.HackResults;

/**
 * JAX-RS End Point for checking submitted codes with existing test cases.
 * 
 * @author Mohit Reddy
 */
@Path("/hack")
public class Hack implements Serializable {
    private static final Logger LOGGER
        = Logger.getLogger(Hack.class.getName());
    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    /**
     * JAX-RS End Point to checks the custom testcase against a submitted solution
     * 
     * @param hackDetails the {@link HackDetails} containing the custom testcase
     *                    to check
     * @return the {@link HackResults} containing the hack status
     */
    @Path("/submit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HackResults CheckHack(HackDetails hackDetails) {
        HackResults hackResults = new HackResults();
        try {
            if (hackDetails.correct_code == null) {
                throw new Exception("Correct code not provided to the service.");
            }
            if (hackDetails.submitted_code == null) {
                throw new Exception(
                    "Submitted code of the user not provided to the service.");
            }
            if (hackDetails.hack_input == null) {
                throw new Exception(
                    "Hack input is not provided to the service");
            }
            JudgeHack callable = new JudgeHack(hackDetails);
            Future<HackResults> submissionResultsFuture
                = threadPool.submit(callable);
            hackResults = submissionResultsFuture.get();
        } catch (Exception e) {
            LOGGER.severe("Error judging the submission. Error: " + e);
        }
        return hackResults;
    }
}
