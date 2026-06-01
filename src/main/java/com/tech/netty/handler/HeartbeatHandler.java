package com.tech.netty.handler;

import com.tech.netty.protocol.ProtocolConstants;
import com.tech.netty.protocol.ProtocolFrame;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(ProtocolFrame.ping(0));
                return;
            }
            if (idleStateEvent.state() == IdleState.READER_IDLE
                    || idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.info("Closing idle connection: {}", ctx.channel().remoteAddress());
                ctx.close();
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ProtocolFrame frame) {
            if (frame.getMsgType() == ProtocolConstants.MSG_TYPE_PING) {
                ctx.writeAndFlush(ProtocolFrame.pong(frame.getSeqId()));
                return;
            }
            if (frame.getMsgType() == ProtocolConstants.MSG_TYPE_PONG) {
                return;
            }
        }
        super.channelRead(ctx, msg);
    }
}
