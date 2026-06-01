package com.tech.netty.websocket;

import com.tech.netty.config.JackalNettyProperties;
import com.tech.netty.core.ChannelInitializerConfigurer;
import com.tech.netty.handler.ConnectionLifecycleHandler;
import com.tech.netty.handler.HeartbeatHandler;
import com.tech.netty.handler.NettyMessageHandler;
import com.tech.netty.handler.ProtocolDispatchHandler;
import com.tech.netty.protocol.ProtocolCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final JackalNettyProperties properties;
    private final ProtocolCodec protocolCodec;
    private final HeartbeatHandler heartbeatHandler;
    private final ConnectionLifecycleHandler lifecycleHandler;
    private final NettyMessageHandler messageHandler;
    private final ChannelInitializerConfigurer configurer;

    public WebSocketChannelInitializer(
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
        JackalNettyProperties.WebSocket ws = properties.getWebsocket();
        JackalNettyProperties.Heartbeat hb = properties.getHeartbeat();
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler(ws.getPath(), null, true));
        pipeline.addLast(
                "idleState",
                new IdleStateHandler(
                        hb.getReaderIdleSeconds(),
                        hb.getWriterIdleSeconds(),
                        hb.getAllIdleSeconds(),
                        TimeUnit.SECONDS));
        pipeline.addLast("wsCodec", new WebSocketBinaryFrameCodec(protocolCodec));
        pipeline.addLast("heartbeat", heartbeatHandler);
        pipeline.addLast("lifecycle", lifecycleHandler);
        pipeline.addLast("dispatch", new ProtocolDispatchHandler(messageHandler));
        if (configurer != null) {
            configurer.configure(pipeline);
        }
    }
}
