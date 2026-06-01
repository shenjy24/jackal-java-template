## ADDED Requirements

### Requirement: WebSocket 服务端独立端口与路径

系统 MUST 在 `jackal.netty.websocket.enabled=true` 时，于 `jackal.netty.websocket.port` 监听 HTTP，并在 `jackal.netty.websocket.path` 完成 WebSocket 握手升级。

#### Scenario: 成功握手

- **WHEN** 客户端对配置路径发起合法 WebSocket Upgrade 请求
- **THEN** 连接升级为 WebSocket，后续可收发二进制帧

#### Scenario: 错误路径

- **WHEN** 请求路径与配置 path 不一致
- **THEN** 系统 MUST 拒绝升级（返回 HTTP 4xx），不进入业务 Handler

### Requirement: WebSocket 二进制载荷与 TCP 协议一致

WebSocket 上使用的二进制消息载荷 MUST 与 TCP 通道使用相同的协议帧字节序列（经 `BinaryProtocolEncoder`/`Decoder` 或等价转换后的 `ProtocolFrame`）。

#### Scenario: 收发 BinaryWebSocketFrame

- **WHEN** 客户端发送 `BinaryWebSocketFrame`，内容为完整协议帧字节
- **THEN** 系统解码为 `ProtocolFrame` 并交由与 TCP 相同的业务处理接口

#### Scenario: 服务端回复

- **WHEN** 业务 Handler 返回 `ProtocolFrame`
- **THEN** 系统编码为字节并通过 `BinaryWebSocketFrame` 写回客户端

### Requirement: WebSocket Pipeline 包含心跳

WebSocket 升级完成后的 Pipeline MUST 与 TCP 一致，包含 `IdleStateHandler` 与 `HeartbeatHandler`，并适用相同的心跳与空闲关闭规则。

#### Scenario: WS 写空闲心跳

- **WHEN** WebSocket 连接在写空闲周期内无出站数据
- **THEN** 系统通过 `BinaryWebSocketFrame` 发送 `PING` 协议帧

### Requirement: WebSocket 可独立开关

系统 MUST 支持仅启用 TCP、仅启用 WebSocket 或同时启用，互不强制依赖。

#### Scenario: 仅 WebSocket

- **WHEN** TCP 禁用且 WebSocket 启用
- **THEN** 仅 WebSocket 端口监听，TCP 端口未绑定
