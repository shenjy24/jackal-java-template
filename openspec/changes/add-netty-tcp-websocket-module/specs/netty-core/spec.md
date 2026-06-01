## ADDED Requirements

### Requirement: Netty 服务可配置启用与禁用

系统 MUST 通过配置项 `jackal.netty.enabled` 控制 Netty 基础设施是否加载；当为 `false` 时，不得启动任何 Netty 服务端线程或绑定端口。

#### Scenario: 全局禁用

- **WHEN** `jackal.netty.enabled` 为 `false`
- **THEN** 应用启动过程中不创建 Netty ServerBootstrap 且不监听 TCP/WebSocket 端口

#### Scenario: 全局启用

- **WHEN** `jackal.netty.enabled` 为 `true` 且子服务（TCP/WebSocket）至少一项启用
- **THEN** 系统在 Spring 应用就绪后按配置启动对应 Netty 服务端

### Requirement: 共享 Boss 与 Worker 线程组管理

系统 MUST 为 Netty 服务端提供可配置的 Boss 线程数；Worker 线程数为 0 时 MUST 使用 Netty 默认（`CPU核心数 * 2`）。

#### Scenario: 使用默认 Worker 线程

- **WHEN** `jackal.netty.tcp.boss-threads` 已配置且 `worker-threads` 为 0
- **THEN** ServerBootstrap 使用默认 EventLoopGroup 规模处理 I/O

#### Scenario: 自定义线程数

- **WHEN** Boss 与 Worker 线程数均配置为正整数
- **THEN** 系统使用指定大小的 NioEventLoopGroup 创建 Channel

### Requirement: 优雅停机

系统 MUST 在 Spring 容器关闭或 `SmartLifecycle` 停止阶段，对 Netty EventLoopGroup 调用 `shutdownGracefully`，并在合理超时内释放端口。

#### Scenario: 应用关闭

- **WHEN** Spring 上下文开始销毁
- **THEN** 已绑定的 Netty 服务端停止接受新连接并关闭现有 EventLoopGroup

### Requirement: Pipeline 扩展点

系统 MUST 提供注册自定义 `ChannelHandler` 的机制（如 `ChannelInitializerConfigurer`），使业务可在默认编解码器之外追加 Handler，且不得破坏默认帧解码顺序。

#### Scenario: 业务注册 Handler

- **WHEN** 应用提供已注册的 Pipeline 配置 Bean
- **THEN** TCP 或 WebSocket Channel 初始化时，在协议编解码器之后（或文档约定位置）加入该 Handler

### Requirement: IdleStateHandler 心跳基础设施

当 Netty 服务端启用时，每个入站 Channel 的 Pipeline MUST 在协议编解码器之前装配 `IdleStateHandler`，且读/写/全空闲秒数 MUST 可通过 `jackal.netty.heartbeat.*` 配置。

#### Scenario: 写空闲发送心跳

- **WHEN** 在配置周期内 Channel 无出站数据且触发 `WRITER_IDLE`
- **THEN** `HeartbeatHandler` MUST 发送系统 `PING` 协议帧（`msgType=0x0001`）

#### Scenario: 读空闲关闭连接

- **WHEN** 在 `reader-idle-seconds` 内未收到任何入站数据且触发 `READER_IDLE`
- **THEN** 系统 MUST 关闭该 Channel 并记录空闲断开日志

#### Scenario: 收到 PING 回复 PONG

- **WHEN** 解码得到系统 `PING` 帧（`msgType=0x0001`）
- **THEN** `HeartbeatHandler` MUST 回复 `PONG` 帧（`msgType=0x0002`），且不得将 PING/PONG 交给业务 `NettyMessageHandler`
