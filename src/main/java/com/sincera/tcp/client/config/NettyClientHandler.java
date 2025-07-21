package com.sincera.tcp.client.config;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String response = (msg instanceof ByteBuf)
                ? ((ByteBuf) msg).toString(CharsetUtil.UTF_8)
                : String.valueOf(msg);
        log.info("Received response from server: {}", response.trim());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in TCP client: {}", cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("TCP connection to server closed/inactive.");
    }
}
