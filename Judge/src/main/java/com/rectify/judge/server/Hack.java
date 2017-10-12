package com.rectify.judge.server;

import com.rectify.judge.runners.JudgeHack;
import com.rectify.judge.utils.HackDetails;
import com.rectify.judge.utils.HackResults;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Created by mohit on 13/10/17.
 */
@Path("/hack")
public class Hack implements Serializable {
    private static final Logger LOGGER = Logger.getLogger("judge");
    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

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
                throw new Exception("Submitted code of the user not provided to the service.");
            }
            if (hackDetails.hack_input == null) {
                throw new Exception("hack input is not provided to the service.");
            }
            JudgeHack callable = new JudgeHack(hackDetails);
            Future<HackResults> submissionResultsFuture = threadPool.submit(callable);
            hackResults = submissionResultsFuture.get();
        } catch (Exception e) {
            LOGGER.severe("Error judging the submission. Error: " + e);
        }
        return hackResults;
    }
}
