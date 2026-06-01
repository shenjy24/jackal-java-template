package com.tech.netty.config;

import com.tech.netty.core.ChannelInitializerConfigurer;
import com.tech.netty.core.NettyServer;
import com.tech.netty.handler.ConnectionLifecycleHandler;
import com.tech.netty.handler.EchoProtocolHandler;
import com.tech.netty.handler.HeartbeatHandler;
import com.tech.netty.handler.NettyMessageHandler;
import com.tech.netty.protocol.DefaultProtocolCodec;
import com.tech.netty.protocol.ProtocolCodec;
import com.tech.netty.tcp.TcpNettyServer;
import com.tech.netty.websocket.WebSocketNettyServer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "jackal.netty", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(JackalNettyProperties.class)
public class NettyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProtocolCodec protocolCodec() {
        return new DefaultProtocolCodec();
    }

    @Bean
    public HeartbeatHandler heartbeatHandler() {
        return new HeartbeatHandler();
    }

    @Bean
    public ConnectionLifecycleHandler connectionLifecycleHandler() {
        return new ConnectionLifecycleHandler();
    }

    @Bean
    @ConditionalOnMissingBean(NettyMessageHandler.class)
    public NettyMessageHandler nettyMessageHandler() {
        return new EchoProtocolHandler();
    }

    @Bean
    @ConditionalOnProperty(prefix = "jackal.netty.tcp", name = "enabled", havingValue = "true", matchIfMissing = true)
    public NettyServer tcpNettyServer(
            JackalNettyProperties properties,
            ProtocolCodec protocolCodec,
            HeartbeatHandler heartbeatHandler,
            ConnectionLifecycleHandler lifecycleHandler,
            NettyMessageHandler messageHandler,
            ObjectProvider<ChannelInitializerConfigurer> configurerProvider) {
        return new TcpNettyServer(
                properties,
                protocolCodec,
                heartbeatHandler,
                lifecycleHandler,
                messageHandler,
                configurerProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnProperty(prefix = "jackal.netty.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
    public NettyServer webSocketNettyServer(
            JackalNettyProperties properties,
            ProtocolCodec protocolCodec,
            HeartbeatHandler heartbeatHandler,
            ConnectionLifecycleHandler lifecycleHandler,
            NettyMessageHandler messageHandler,
            ObjectProvider<ChannelInitializerConfigurer> configurerProvider) {
        return new WebSocketNettyServer(
                properties,
                protocolCodec,
                heartbeatHandler,
                lifecycleHandler,
                messageHandler,
                configurerProvider.getIfAvailable());
    }

    @Bean
    public NettyServerLifecycle nettyServerLifecycle(ObjectProvider<NettyServer> servers) {
        return new NettyServerLifecycle(servers.orderedStream().toList());
    }
}
