package com.tech.netty.core;

import com.tech.netty.config.JackalNettyProperties;
import com.tech.netty.handler.ConnectionLifecycleHandler;
import com.tech.netty.handler.HeartbeatHandler;
import com.tech.netty.handler.NettyMessageHandler;
import com.tech.netty.handler.ProtocolDispatchHandler;
import com.tech.netty.protocol.BinaryProtocolDecoder;
import com.tech.netty.protocol.BinaryProtocolEncoder;
import com.tech.netty.protocol.ProtocolCodec;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public final class ProtocolPipelineSupport {

    private ProtocolPipelineSupport() {
    }

    public static void addProtocolHandlers(
            ChannelPipeline pipeline,
            JackalNettyProperties properties,
            ProtocolCodec codec,
            HeartbeatHandler heartbeatHandler,
            ConnectionLifecycleHandler lifecycleHandler,
            NettyMessageHandler messageHandler,
            ChannelInitializerConfigurer configurer) {
        JackalNettyProperties.Heartbeat hb = properties.getHeartbeat();
        pipeline.addLast("lifecycle", lifecycleHandler);
        pipeline.addLast(
                "idleState",
                new IdleStateHandler(
                        hb.getReaderIdleSeconds(),
                        hb.getWriterIdleSeconds(),
                        hb.getAllIdleSeconds(),
                        TimeUnit.SECONDS));
        pipeline.addLast("decoder", new BinaryProtocolDecoder(codec, properties.getProtocol().getMaxFrameLength()));
        pipeline.addLast("heartbeat", heartbeatHandler);
        pipeline.addLast("dispatch", new ProtocolDispatchHandler(messageHandler));
        pipeline.addLast("encoder", new BinaryProtocolEncoder(codec));
        if (configurer != null) {
            configurer.configure(pipeline);
        }
    }
}
