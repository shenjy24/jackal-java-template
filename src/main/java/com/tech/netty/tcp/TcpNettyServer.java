package com.tech.netty.tcp;

import com.tech.netty.config.JackalNettyProperties;
import com.tech.netty.core.ChannelInitializerConfigurer;
import com.tech.netty.core.NettyServer;
import com.tech.netty.handler.ConnectionLifecycleHandler;
import com.tech.netty.handler.HeartbeatHandler;
import com.tech.netty.handler.NettyMessageHandler;
import com.tech.netty.protocol.ProtocolCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TcpNettyServer implements NettyServer {

    private final JackalNettyProperties properties;
    private final ProtocolCodec protocolCodec;
    private final HeartbeatHandler heartbeatHandler;
    private final ConnectionLifecycleHandler lifecycleHandler;
    private final NettyMessageHandler messageHandler;
    private final ChannelInitializerConfigurer configurer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @Override
    public void start() throws InterruptedException {
        JackalNettyProperties.Tcp tcp = properties.getTcp();
        int workers = tcp.getWorkerThreads() > 0 ? tcp.getWorkerThreads() : 0;
        bossGroup = new NioEventLoopGroup(tcp.getBossThreads());
        workerGroup = workers > 0 ? new NioEventLoopGroup(workers) : new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(
                        new TcpChannelInitializer(
                                properties,
                                protocolCodec,
                                heartbeatHandler,
                                lifecycleHandler,
                                messageHandler,
                                configurer));

        ChannelFuture future = bootstrap.bind(tcp.getPort()).sync();
        serverChannel = future.channel();
        log.info("TCP Netty server started on port {}", tcp.getPort());
    }

    @Override
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        shutdownGroup(bossGroup);
        shutdownGroup(workerGroup);
        bossGroup = null;
        workerGroup = null;
        serverChannel = null;
        log.info("TCP Netty server stopped");
    }

    @Override
    public boolean isRunning() {
        return serverChannel != null && serverChannel.isActive();
    }

    private static void shutdownGroup(EventLoopGroup group) {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
