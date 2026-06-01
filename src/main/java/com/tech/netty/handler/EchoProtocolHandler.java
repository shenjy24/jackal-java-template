package com.tech.netty.handler;

import com.tech.netty.protocol.ProtocolConstants;
import com.tech.netty.protocol.ProtocolFrame;
import io.netty.channel.ChannelHandlerContext;
public class EchoProtocolHandler implements NettyMessageHandler {

    @Override
    public void onMessage(ChannelHandlerContext ctx, ProtocolFrame frame) {
        if (ProtocolConstants.isSystemMessage(frame.getMsgType())) {
            return;
        }
        ctx.writeAndFlush(frame);
    }
}
