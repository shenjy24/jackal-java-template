package com.tech.netty.protocol;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolFrame {

    private byte version;
    private int msgType;
    private byte flags;
    private int seqId;
    private byte[] body;

    public static ProtocolFrame ping(int seqId) {
        return ProtocolFrame.builder()
                .version(ProtocolConstants.VERSION)
                .msgType(ProtocolConstants.MSG_TYPE_PING)
                .flags((byte) 0)
                .seqId(seqId)
                .body(new byte[0])
                .build();
    }

    public static ProtocolFrame pong(int seqId) {
        return ProtocolFrame.builder()
                .version(ProtocolConstants.VERSION)
                .msgType(ProtocolConstants.MSG_TYPE_PONG)
                .flags((byte) 0)
                .seqId(seqId)
                .body(new byte[0])
                .build();
    }
}
