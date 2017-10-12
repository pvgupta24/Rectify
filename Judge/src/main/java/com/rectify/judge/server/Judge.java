package com.rectify.judge.server;

import com.rectify.judge.runners.JudgeSubmission;
import com.rectify.judge.utils.SubmissionDetails;
import com.rectify.judge.utils.SubmissionResults;

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
 * Created by mohit on 11/10/17.
 */

@Path("/code")
public class Judge implements Serializable{

    private static final Logger LOGGER = Logger.getLogger("judge");
    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @Path("/submit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SubmissionResults CheckSolution(SubmissionDetails userSubmission) {
        SubmissionResults submissionResults = new SubmissionResults();
        try {
            if (userSubmission.problemId == null) {
                throw new Exception("ProblemId not provided to the service.");
            }
            if (userSubmission.userId == null) {
                throw new Exception("EmailId of the user not provided to the service.");
            }
            if (userSubmission.code == null) {
                throw new Exception("Code is not provided to the service.");
            }
            JudgeSubmission callable = new JudgeSubmission(userSubmission);
            Future<SubmissionResults> submissionResultsFuture = threadPool.submit(callable);
            submissionResults = submissionResultsFuture.get();
        } catch (Exception e) {
            LOGGER.severe("Error judging the submission. Error: " + e);
        }
        return submissionResults;
    }
}
