package com.tech.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class BinaryProtocolDecoder extends ByteToMessageDecoder {

    private final ProtocolCodec codec;
    private final int maxFrameLength;

    public BinaryProtocolDecoder(ProtocolCodec codec, int maxFrameLength) {
        this.codec = codec;
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < ProtocolConstants.HEADER_SIZE) {
            return;
        }
        in.markReaderIndex();
        short magic = in.getShort(in.readerIndex());
        if (magic != ProtocolConstants.MAGIC) {
            in.resetReaderIndex();
            ctx.close();
            return;
        }
        int bodyLength = in.getInt(in.readerIndex() + 10);
        int frameLength =
                ProtocolConstants.HEADER_SIZE + bodyLength + ProtocolConstants.CHECKSUM_SIZE;
        if (bodyLength < 0 || frameLength > maxFrameLength) {
            in.resetReaderIndex();
            ctx.close();
            return;
        }
        if (in.readableBytes() < frameLength) {
            in.resetReaderIndex();
            return;
        }
        try {
            ProtocolFrame frame = codec.decodeFrame(in);
            if (frame != null) {
                out.add(frame);
            }
        } catch (ProtocolDecodeException e) {
            in.resetReaderIndex();
            ctx.close();
        }
    }
}
