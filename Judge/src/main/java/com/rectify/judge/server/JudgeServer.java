package com.rectify.judge.server;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starts a web server which listens for requests for running and testing the
 * code submissions and hacks.  
 *  
 * @author Mohit Reddy
 */
public class JudgeServer {

    private static final Logger LOGGER
        = Logger.getLogger(JudgeServer.class.getName());
    public static final String HOST = "http://0.0.0.0";
    private static final String PORT = "9002";

    private HttpServer createServer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        final ResourceConfig resourceConfig  = new ResourceConfig().packages(
            JudgeServer.class.getPackage().getName());
        resourceConfig.register(provider);

        String serverUri = HOST + ":" + PORT + "/";
        return GrizzlyHttpServerFactory.createHttpServer(
            URI.create(serverUri), resourceConfig);
    }

    public static void main(String[] args)
        throws IOException, InterruptedException
    {
        // System.out.println("Hello ");
        // SandboxTest sb = new SandboxTest();
        // sb.runContainer();
        // System.out.println("Docker completed");
        try {
            JudgeServer judgeServer = new JudgeServer();

            final HttpServer server = judgeServer.createServer();

            // Registers shutdown hook for closing the server.
            Runtime.getRuntime().addShutdownHook(
                new Thread( () -> {
                    LOGGER.info("Stopping server..");
                    server.shutdownNow();
                }, "shutdownhook")
            );
            server.start();
            LOGGER.info("Judge server started at localhost" + PORT);
            LOGGER.info("\nPress CTRL + C to exit..\n");
            Thread.currentThread().join();
        } catch (Exception | Error e) {
            LOGGER.log(Level.SEVERE,
                "Error starting Grizzly HTTP Judge Server : " + e);
        }
    }
}
