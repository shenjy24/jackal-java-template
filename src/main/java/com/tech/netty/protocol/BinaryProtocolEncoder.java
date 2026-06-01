package com.tech.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class BinaryProtocolEncoder extends MessageToByteEncoder<ProtocolFrame> {

    private final ProtocolCodec codec;

    public BinaryProtocolEncoder(ProtocolCodec codec) {
        super(ProtocolFrame.class);
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolFrame msg, ByteBuf out) {
        codec.encodeFrame(msg, out);
    }
}
