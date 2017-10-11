package com.rectify.judge.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mohit on 11/10/17.
 */
public class JudgeServer {

    private static final Logger LOGGER = Logger.getLogger("judge_log");

    private HttpServer createServer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        final ResourceConfig rc = new ResourceConfig().packages("com.rectify.judge.server");
        rc.register(provider);

        String serverUri = "http://0.0.0.0" + ":" + "9002" + "/";
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(serverUri), rc);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            JudgeServer judgeServer = new JudgeServer();

            final HttpServer server = judgeServer.createServer();
            // Register shutdown hook.
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    LOGGER.info("Stopping server..");
                    server.stop();
                }
            }, "shutdownhook"));
            server.start();
            LOGGER.info("Judge server started at localhost 9002");
            LOGGER.info("\nPress CTRL + C to exit..\n");
            Thread.currentThread().join();
        } catch (Exception | Error e) {
            final String errorHelper = "There was an error while starting Grizzly HTTP Judge Server..";
            LOGGER.log(Level.SEVERE, errorHelper + e);

        }
    }
}
