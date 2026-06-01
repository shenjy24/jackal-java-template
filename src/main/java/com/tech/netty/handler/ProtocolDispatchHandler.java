package com.tech.netty.handler;

import com.tech.netty.protocol.ProtocolFrame;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ProtocolDispatchHandler extends SimpleChannelInboundHandler<ProtocolFrame> {

    private final NettyMessageHandler messageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolFrame msg) throws Exception {
        messageHandler.onMessage(ctx, msg);
    }
}
