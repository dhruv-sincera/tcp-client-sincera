package com.sincera.tcp.client.config;

import com.sincera.tcp.client.service.JsonMessageSenderService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    private EventLoopGroup group;
    private Channel channel;

    @PostConstruct
    public void start() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                    }
                });

        ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
        channel = future.channel();
        log.info("Connected to TCP server ---> {}:{}", HOST, PORT);
    }

    public void sendDirect(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message + "\n");
        } else {
            throw new IllegalStateException("TCP channel not active");
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
