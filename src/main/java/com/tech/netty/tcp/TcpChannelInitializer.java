package com.tech.netty.tcp;

import com.tech.netty.config.JackalNettyProperties;
import com.tech.netty.core.ChannelInitializerConfigurer;
import com.tech.netty.core.ProtocolPipelineSupport;
import com.tech.netty.handler.ConnectionLifecycleHandler;
import com.tech.netty.handler.HeartbeatHandler;
import com.tech.netty.handler.NettyMessageHandler;
import com.tech.netty.protocol.ProtocolCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final JackalNettyProperties properties;
    private final ProtocolCodec protocolCodec;
    private final HeartbeatHandler heartbeatHandler;
    private final ConnectionLifecycleHandler lifecycleHandler;
    private final NettyMessageHandler messageHandler;
    private final ChannelInitializerConfigurer configurer;

    public TcpChannelInitializer(
            JackalNettyProperties properties,
            ProtocolCodec protocolCodec,
            HeartbeatHandler heartbeatHandler,
            ConnectionLifecycleHandler lifecycleHandler,
            NettyMessageHandler messageHandler,
            ChannelInitializerConfigurer configurer) {
        this.properties = properties;
        this.protocolCodec = protocolCodec;
        this.heartbeatHandler = heartbeatHandler;
        this.lifecycleHandler = lifecycleHandler;
        this.messageHandler = messageHandler;
        this.configurer = configurer;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ProtocolPipelineSupport.addProtocolHandlers(
                ch.pipeline(),
                properties,
                protocolCodec,
                heartbeatHandler,
                lifecycleHandler,
                messageHandler,
                configurer);
    }
}
