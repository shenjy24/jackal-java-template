package com.tech.netty.config;

import com.tech.netty.core.NettyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class NettyServerLifecycle implements SmartLifecycle {

    private final List<NettyServer> servers;
    private volatile boolean running;

    @Override
    public void start() {
        for (NettyServer server : servers) {
            try {
                server.start();
            } catch (Exception e) {
                log.error("Failed to start Netty server: {}", server.getClass().getSimpleName(), e);
                throw new IllegalStateException("Netty server start failed", e);
            }
        }
        running = true;
    }

    @Override
    public void stop() {
        for (NettyServer server : servers) {
            try {
                server.stop();
            } catch (Exception e) {
                log.warn("Error stopping Netty server: {}", server.getClass().getSimpleName(), e);
            }
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
