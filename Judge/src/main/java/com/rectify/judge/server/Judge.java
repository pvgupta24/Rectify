package com.rectify.judge.server;

import java.io.Serializable;
import java.util.concurrent.*;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.rectify.judge.runners.JudgeSubmission;
import com.rectify.judge.utils.SubmissionDetails;
import com.rectify.judge.utils.SubmissionResults;

/**
 * JAX-RS End Point for running the submitted code against given test cases and
 * expected output.
 * 
 * @author Mohit Reddy
 */
@Path("/code")
public class Judge implements Serializable {

    private static final Logger LOGGER
        = Logger.getLogger(Judge.class.getName());
    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    /**
     * JAX-RS End Point to checks the user submission against the existing test cases.
     * 
     * @param userSubmission the {@link SubmissionDetails} containing the code to
     *                       check and user, problem details
     * @return the {@link SubmissionResults} containing the status
     */
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
                throw new Exception(
                    "EmailId of the user not provided to theservice.");
            }
            if (userSubmission.code == null) {
                throw new Exception("Code is not provided to the service.");
            }
            JudgeSubmission callable = new JudgeSubmission(userSubmission);
            Future<SubmissionResults> submissionResultsFuture
                = threadPool.submit(callable);
            submissionResults = submissionResultsFuture.get();
        } catch (Exception e) {
            LOGGER.severe("Error judging the submission. Error: " + e);
        }

        return submissionResults;
    }
}
