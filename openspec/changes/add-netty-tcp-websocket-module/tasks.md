## 1. 依赖与包结构

- [x] 1.1 在根 `pom.xml` 引入 Netty BOM 及所需模块依赖（transport、handler、codec、codec-http）
- [x] 1.2 创建 `com.tech.netty` 包结构：`core`、`protocol`、`tcp`、`websocket`、`config`、`handler`

## 2. 配置与自动装配（netty-core）

- [x] 2.1 定义 `JackalNettyProperties`（`jackal.netty` 前缀）及 TCP/WebSocket/Protocol/Heartbeat 子配置
- [x] 2.2 实现 `NettyAutoConfiguration`，使用 `@ConditionalOnProperty` 控制全局与子服务开关
- [x] 2.3 实现 `NettyServerLifecycle`（`SmartLifecycle`）：应用就绪后启动、关闭时 `shutdownGracefully`
- [x] 2.4 实现 `ChannelInitializerConfigurer` 扩展点及默认空实现注册方式

## 3. 二进制协议（netty-binary-protocol）

- [x] 3.1 定义 `ProtocolFrame`、`ProtocolCodec` 接口、帧头常量及系统消息类型（`PING=0x0001`、`PONG=0x0002`）
- [x] 3.2 实现 `BinaryProtocolDecoder`（半包/粘包、max-frame-length、魔数/版本校验、**必选 CRC32 校验**）
- [x] 3.3 实现 `BinaryProtocolEncoder`（大端序写入帧头、Body 与 **CRC32**）
- [x] 3.4 支持通过 Spring Bean 覆盖默认 `ProtocolCodec`

## 4. 心跳（netty-core + TCP/WS）

- [x] 4.1 实现 `HeartbeatHandler`：处理 `IdleStateEvent`、发送 `PING`、响应 `PONG`、读空闲关闭连接
- [x] 4.2 在 TCP/WebSocket `ChannelInitializer` 中装配 `IdleStateHandler`（读/写/全空闲秒数来自配置）

## 5. TCP 服务端（netty-tcp-server）

- [x] 5.1 实现 `TcpNettyServer`：`ServerBootstrap`、Boss/Worker 线程组、绑定配置端口
- [x] 5.2 实现 `TcpChannelInitializer`：Logging（可选）→ IdleState → Heartbeat → 编解码 → 业务 Handler
- [x] 5.3 实现 `NettyMessageHandler` 抽象或接口，并提供 `EchoProtocolHandler` 示例（忽略 PING/PONG）
- [x] 5.4 连接激活/断开日志与异常 `exceptionCaught` 统一处理

## 6. WebSocket 服务端（netty-websocket-server）

- [x] 6.1 实现 `WebSocketNettyServer`：HTTP 编解码、`WebSocketServerProtocolHandler`、配置 path
- [x] 6.2 实现 `WebSocketFrame` 与协议帧互转 Handler（`BinaryWebSocketFrame` ↔ `ProtocolFrame`）
- [x] 6.3 升级后 Pipeline 复用 IdleState、Heartbeat、编解码与 `NettyMessageHandler`
- [x] 6.4 错误路径与非法升级请求返回 4xx

## 7. 集成与文档

- [x] 7.1 在 `application-local.yml`（或 `application-dev.yml`）添加 `jackal.netty` 示例（含 `heartbeat` 配置，默认 `enabled: false`）
- [x] 7.2 在 `readme.md` 或 `document/` 补充：端口、CRC32 帧格式、PING/PONG 约定、自定义 Handler 接入
- [x] 7.3 本地验证：TCP/WS 合法帧往返、CRC 错误拒收、写空闲 PING、读空闲断连

## 8. 测试（建议）

- [x] 8.1 `EmbeddedChannel` 测试编解码与 CRC32 失败路径
- [x] 8.2 `EmbeddedChannel` 测试 `HeartbeatHandler` 的 PING/PONG 与空闲关闭
