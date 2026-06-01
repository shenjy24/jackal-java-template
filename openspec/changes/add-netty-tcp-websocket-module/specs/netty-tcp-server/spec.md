## ADDED Requirements

### Requirement: TCP 服务端独立端口监听

系统 MUST 在 `jackal.netty.tcp.enabled=true` 时，于 `jackal.netty.tcp.port` 指定端口启动 NIO TCP 服务端。

#### Scenario: 成功启动 TCP 服务

- **WHEN** 全局 Netty 已启用且 TCP 已启用，端口未被占用
- **THEN** 服务端在该端口监听入站 TCP 连接，并记录绑定地址日志

#### Scenario: TCP 禁用

- **WHEN** `jackal.netty.tcp.enabled` 为 `false`
- **THEN** 系统不得绑定 TCP 端口

### Requirement: TCP Pipeline 包含二进制协议编解码

每个新建立的 TCP Channel MUST 按顺序装配 `IdleStateHandler`、`HeartbeatHandler`、帧解码器、帧编码器及业务消息处理入口（`NettyMessageHandler` 或等价扩展点）。

#### Scenario: 收到合法二进制帧

- **WHEN** 客户端发送符合协议规范的完整帧
- **THEN** Pipeline 将字节流解码为 `ProtocolFrame` 并 dispatch 至业务 Handler

#### Scenario: 收到非法或超长帧

- **WHEN** 帧长度超过 `jackal.netty.protocol.max-frame-length` 或魔数/版本不匹配
- **THEN** 系统 MUST 关闭该 Channel 或丢弃并记录错误，不得无限缓冲

### Requirement: 连接生命周期可观测

系统 MUST 在 Channel 激活与断开时输出可配置的调试/信息级日志（连接远端地址、ChannelId）。

#### Scenario: 客户端连接

- **WHEN** TCP 三次握手完成且 Channel 激活
- **THEN** 日志中包含远端 IP 与端口（或等价标识）
