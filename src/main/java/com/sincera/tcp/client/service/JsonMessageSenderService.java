package com.sincera.tcp.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

@Service
public class JsonMessageSenderService {

    private final String serverHost;
    private final int serverPort;

    private static final Logger log = LoggerFactory.getLogger(JsonMessageSenderService.class);

    public JsonMessageSenderService(
            @Value("${tcp.server.host}") String serverHost,
            @Value("${tcp.server.port}") int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void startSendingMessages(String message) {
        try (Socket socket = new Socket(serverHost, serverPort)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // Send the message to the TCP server
            writer.println(message);

        }  catch (Exception ex) {
            log.error("Client exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
