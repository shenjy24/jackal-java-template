package com.tech.netty.protocol;

import io.netty.buffer.ByteBuf;

import java.util.zip.CRC32;

final class Crc32Util {

    private Crc32Util() {
    }

    static int compute(ByteBuf buf, int startIndex, int length) {
        CRC32 crc32 = new CRC32();
        byte[] bytes = new byte[Math.min(length, 4096)];
        int readerIndex = buf.readerIndex();
        buf.readerIndex(startIndex);
        int remaining = length;
        while (remaining > 0) {
            int chunk = Math.min(remaining, bytes.length);
            buf.readBytes(bytes, 0, chunk);
            crc32.update(bytes, 0, chunk);
            remaining -= chunk;
        }
        buf.readerIndex(readerIndex);
        return (int) crc32.getValue();
    }
}
