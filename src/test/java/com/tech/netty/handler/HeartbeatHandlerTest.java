package com.tech.netty.handler;

import com.tech.netty.protocol.BinaryProtocolDecoder;
import com.tech.netty.protocol.BinaryProtocolEncoder;
import com.tech.netty.protocol.DefaultProtocolCodec;
import com.tech.netty.protocol.ProtocolConstants;
import com.tech.netty.protocol.ProtocolFrame;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeartbeatHandlerTest {

    private final DefaultProtocolCodec codec = new DefaultProtocolCodec();

    @Test
    void pingTriggersPong() {
        EmbeddedChannel channel = new EmbeddedChannel(new HeartbeatHandler());
        channel.writeInbound(ProtocolFrame.ping(7));
        ProtocolFrame pong = channel.readOutbound();
        assertEquals(ProtocolConstants.MSG_TYPE_PONG, pong.getMsgType());
        assertEquals(7, pong.getSeqId());
    }

    @Test
    void writerIdleSendsPing() {
        EmbeddedChannel channel =
                new EmbeddedChannel(
                        new HeartbeatHandler(),
                        new BinaryProtocolEncoder(codec));

        channel.pipeline().fireUserEventTriggered(IdleStateEvent.WRITER_IDLE_STATE_EVENT);
        ProtocolFrame ping = channel.readOutbound();
        assertEquals(ProtocolConstants.MSG_TYPE_PING, ping.getMsgType());
    }

    @Test
    void readerIdleClosesChannel() {
        EmbeddedChannel channel = new EmbeddedChannel(new HeartbeatHandler());
        channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT);
        assertFalse(channel.isActive());
    }
}
