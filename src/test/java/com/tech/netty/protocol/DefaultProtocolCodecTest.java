package com.tech.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultProtocolCodecTest {

    private final DefaultProtocolCodec codec = new DefaultProtocolCodec();

    @Test
    void encodeAndDecodeRoundTrip() {
        ProtocolFrame original =
                ProtocolFrame.builder()
                        .version(ProtocolConstants.VERSION)
                        .msgType(0x0100)
                        .flags((byte) 0)
                        .seqId(42)
                        .body(new byte[] {1, 2, 3})
                        .build();

        ByteBuf buf = Unpooled.buffer();
        codec.encodeFrame(original, buf);

        ProtocolFrame decoded = codec.decodeFrame(buf);
        assertEquals(original.getMsgType(), decoded.getMsgType());
        assertEquals(original.getSeqId(), decoded.getSeqId());
        assertArrayEquals(original.getBody(), decoded.getBody());
    }

    @Test
    void decodeFailsOnBadCrc() {
        ProtocolFrame frame = ProtocolFrame.ping(1);
        ByteBuf buf = Unpooled.buffer();
        codec.encodeFrame(frame, buf);
        buf.setInt(buf.writerIndex() - 4, 0);

        assertThrows(ProtocolDecodeException.class, () -> codec.decodeFrame(buf));
    }
}
