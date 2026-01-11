package com.example;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;

public class Main {
    // Attention : Vérifiez que ce port (8080) est libre sur votre machine
    public static final String BASE_URI = "http://localhost:8080/starter/api/";

    public static void main(String[] args) throws Exception {
        // Démarre le serveur Grizzly
        final var server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI));

        System.out.println(String.format("Api server is starting on %s\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}