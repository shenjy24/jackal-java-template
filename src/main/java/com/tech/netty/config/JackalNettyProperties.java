package com.tech.netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jackal.netty")
public class JackalNettyProperties {

    private boolean enabled = false;
    private final Tcp tcp = new Tcp();
    private final WebSocket websocket = new WebSocket();
    private final Protocol protocol = new Protocol();
    private final Heartbeat heartbeat = new Heartbeat();

    @Data
    public static class Tcp {
        private boolean enabled = true;
        private int port = 9000;
        private int bossThreads = 1;
        private int workerThreads = 0;
    }

    @Data
    public static class WebSocket {
        private boolean enabled = true;
        private int port = 9001;
        private String path = "/ws";
    }

    @Data
    public static class Protocol {
        private int maxFrameLength = 1048576;
    }

    @Data
    public static class Heartbeat {
        private int readerIdleSeconds = 90;
        private int writerIdleSeconds = 30;
        private int allIdleSeconds = 0;
    }
}
