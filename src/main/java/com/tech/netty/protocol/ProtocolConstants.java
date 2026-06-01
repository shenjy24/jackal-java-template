package com.tech.netty.protocol;

public final class ProtocolConstants {

    public static final short MAGIC = (short) 0x5A5A;
    public static final byte VERSION = 0x01;
    public static final int HEADER_SIZE = 14;
    public static final int CHECKSUM_SIZE = 4;

    public static final int MSG_TYPE_PING = 0x0001;
    public static final int MSG_TYPE_PONG = 0x0002;

    private ProtocolConstants() {
    }

    public static boolean isSystemMessage(int msgType) {
        return msgType == MSG_TYPE_PING || msgType == MSG_TYPE_PONG;
    }
}
