package com.sincera.tcp.client.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class NettyTcpClient {

    private static final Logger log = LoggerFactory.getLogger(NettyTcpClient.class);

    @Value("${tcp.server.host}")
    private String HOST;

    @Value("${tcp.server.port}")
    private int PORT;

    @Value("${tcp.server.timeout}")
    private int TIMEOUT;

    @Value("${tcp.server.message.max.kb.size}")
    private int MAX_KB_SIZE;

    private EventLoopGroup group;
    private Channel channel;

    private final NettyClientHandler nettyClientHandler;

    public NettyTcpClient(NettyClientHandler nettyClientHandler) {
        this.nettyClientHandler = nettyClientHandler;
    }

    @PostConstruct
    public void start() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LineBasedFrameDecoder(MAX_KB_SIZE),
                                    new StringDecoder(CharsetUtil.UTF_8),
                                    new StringEncoder(CharsetUtil.UTF_8),
                                    nettyClientHandler
                            );
                        }
                    });

            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            channel = future.channel();
            log.info("Connected to TCP server at {}:{}", HOST, PORT);
        } catch (Exception e) {
            log.error("Failed to connect to TCP server: {}", e.getMessage(), e);
        }
    }

    public void sendDirect(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message + "\n").addListener(future -> {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    log.error("Failed to send message: {}", cause.getMessage(), cause);
                } else {
                    log.info("Message sent: {}", message);
                }
            });
        } else {
            log.error("TCP channel is not active. Message not sent.");
            throw new IllegalStateException("TCP channel not active");
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (channel != null) {
                channel.close().sync();
            }
            if (group != null) {
                group.shutdownGracefully().sync();
            }
            log.info("Netty TCP client shutdown complete.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error shutting down TCP client: {}", e.getMessage(), e);
        }
    }
}
