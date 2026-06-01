package com.tech.netty.core;

import io.netty.channel.ChannelPipeline;

@FunctionalInterface
public interface ChannelInitializerConfigurer {

    void configure(ChannelPipeline pipeline);
}
