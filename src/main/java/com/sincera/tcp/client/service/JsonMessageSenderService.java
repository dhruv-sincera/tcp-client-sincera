package com.sincera.tcp.client.service;

import com.sincera.tcp.client.config.NettyTcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

@Service
public class JsonMessageSenderService {

    private static final Logger log = LoggerFactory.getLogger(JsonMessageSenderService.class);

    /*private final String serverHost;
    private final int serverPort;
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
            //ex.printStackTrace();
        }
    }*/

    private final NettyTcpClient nettyTcpClient;

    public JsonMessageSenderService(NettyTcpClient nettyTcpClient) {
        this.nettyTcpClient = nettyTcpClient;
    }

    @Async("taskExecutor")
    public void sendMessageAsync(String message) {
        try {
            nettyTcpClient.sendDirect(message);
        } catch (Exception ex) {
            log.error("Failed to send message over TCP: {}", ex.getMessage(), ex);
        }
    }

    public void sendMessageSync(String message) {
        try {
            nettyTcpClient.sendDirect(message);
        } catch (Exception ex) {
            log.error("Failed to send message over TCP: {}", ex.getMessage(), ex);
        }
    }
}
