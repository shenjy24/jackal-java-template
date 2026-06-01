package com.tech.netty.core;

public interface NettyServer {

    void start() throws Exception;

    void stop();

    boolean isRunning();
}
