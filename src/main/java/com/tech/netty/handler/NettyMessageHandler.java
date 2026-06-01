package com.tech.netty.handler;

import com.tech.netty.protocol.ProtocolFrame;
import io.netty.channel.ChannelHandlerContext;

public interface NettyMessageHandler {

    void onMessage(ChannelHandlerContext ctx, ProtocolFrame frame) throws Exception;
}
