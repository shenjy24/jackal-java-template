package com.tech.netty.websocket;

import com.tech.netty.protocol.ProtocolCodec;
import com.tech.netty.protocol.ProtocolDecodeException;
import com.tech.netty.protocol.ProtocolFrame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class WebSocketBinaryFrameCodec extends MessageToMessageCodec<WebSocketFrame, ProtocolFrame> {

    private final ProtocolCodec codec;

    public WebSocketBinaryFrameCodec(ProtocolCodec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolFrame msg, List<Object> out) {
        ByteBuf buf = Unpooled.buffer();
        codec.encodeFrame(msg, buf);
        out.add(new BinaryWebSocketFrame(buf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) {
        if (!(msg instanceof BinaryWebSocketFrame binaryFrame)) {
            return;
        }
        ByteBuf content = binaryFrame.content();
        try {
            ProtocolFrame frame = codec.decodeFrame(content);
            if (frame != null) {
                out.add(frame);
            }
        } catch (ProtocolDecodeException e) {
            ctx.close();
        }
    }
}
