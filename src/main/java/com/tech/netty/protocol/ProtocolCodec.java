package com.tech.netty.protocol;

import io.netty.buffer.ByteBuf;

public interface ProtocolCodec {

    ProtocolFrame decodeFrame(ByteBuf in);

    void encodeFrame(ProtocolFrame frame, ByteBuf out);
}
