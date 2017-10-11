package com.rectify.judge.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by mohit on 11/10/17.
 */

@Path("/code")
public class Judge implements Serializable{

    private static final Logger LOGGER = Logger.getLogger("judge");

    @Path("/submit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> CheckSolution(Map<String, String> userSubmission) {
        Map<String, String> m = new HashMap<>();
        m.put("solution", "correct");
        return m;
    }
}
