## Context

项目为 Spring Boot 3.5 + Java 21 的单体模板（`jackal-java-template`），当前仅具备 Servlet 栈 HTTP API。需要在不破坏现有 Web 层的前提下，引入可复用的 Netty 网络层，同时支持 TCP 长连接与 WebSocket，并统一自定义二进制协议编解码。

约束与约定：

- 包名沿用 `com.tech`，Netty 相关代码建议置于 `com.tech.netty` 或独立子模块 `jackal-netty`
- 与 Spring 生命周期集成，支持通过配置启用/禁用
- 编解码与业务 Handler 解耦，业务通过实现接口或注册 Bean 扩展 Pipeline

## Goals / Non-Goals

**Goals:**

- 提供可配置的 TCP、WebSocket 服务端，共享二进制协议层
- 抽象 `NettyServer` 启动/停止、Boss/Worker 线程组、Channel 初始化
- 定义可扩展的二进制帧格式与默认编解码器（**每帧必选 CRC32**）
- **TCP/WebSocket Pipeline 内置 `IdleStateHandler` 与心跳 Handler**，可配置空闲超时
- Spring Boot `AutoConfiguration` + `@ConfigurationProperties` 管理端口与开关
- 文档与示例 Handler，便于二次开发

**Non-Goals:**

- 不实现具体业务消息（登录、推送路由等）
- 不实现 TLS、认证、限流、分布式 Session（可后续增量）
- 不提供多协议版本协商以外的复杂网关能力
- 不强制拆分为独立部署进程（默认同 JVM，与 Spring 共存）

## Decisions

### 1. 模块结构：单模块包划分 vs Maven 子模块

**选择**：首版在现有 `jackal-java-template` 内以 `com.tech.netty` 包组织；`pom.xml` 仅增加 Netty 依赖。若后续体积或发布需求增加，再拆 `jackal-netty` 子模块。

**理由**：模板项目保持简单；减少多模块 CI 复杂度。

**备选**：独立 `jackal-netty` 子模块 — 更适合多应用复用，首版过重。

### 2. Netty 依赖版本

**选择**：使用 Netty BOM 对齐版本（如 `4.1.118.Final` 或与 Spring Boot 兼容的 BOM 管理版本），依赖 `netty-all` 或精简为 `netty-transport`、`netty-handler`、`netty-codec`、`netty-codec-http`。

**理由**：WebSocket 需要 HTTP 编解码；统一 BOM 避免传递依赖冲突。

### 3. 二进制协议帧格式（默认实现）

**选择**：固定长度帧头 + 变长 Body：


| 字段         | 长度  | 说明                        |
| ---------- | --- | ------------------------- |
| magic      | 2B  | `0x5A5A`                  |
| version    | 1B  | 协议版本，首版 `0x01`            |
| msgType    | 2B  | 消息类型（业务枚举）                |
| flags      | 1B  | 保留（如压缩、ACK 位）             |
| seqId      | 4B  | 序号（可选业务用）                 |
| bodyLength | 4B  | Body 字节数                  |
| body       | N   | 载荷                        |
| checksum   | 4B  | **必选** CRC32（覆盖 magic 至 body 末尾） |


**理由**：长度前缀解决粘包/半包；字段足够表达常见 IoT/实时场景。

**备选**：Protobuf/JSON over WebSocket — 与「自定义二进制」需求不符，可作为后续第二种 `ProtocolCodec`。

### 4. 编解码抽象

**选择**：

- `ProtocolFrame`：领域对象（header + byte[] body）
- `BinaryProtocolDecoder` extends `ByteToMessageDecoder`（基于 `LengthFieldBasedFrameDecoder` 或自研按 header 读满帧）
- `BinaryProtocolEncoder` extends `MessageToByteEncoder<ProtocolFrame>`
- `ProtocolCodec` 接口允许未来替换实现

Pipeline 顺序（TCP）：`LoggingHandler` → `IdleStateHandler` → `HeartbeatHandler` → `BinaryProtocolDecoder` → `BinaryProtocolEncoder` → `BusinessChannelHandler`（用户扩展）

WebSocket：HTTP 升级链之后同样装配 `IdleStateHandler` → `HeartbeatHandler` → 帧编解码 → 业务 Handler；`BinaryWebSocketFrame` 载荷为完整协议帧（含 CRC32）。

**心跳约定**：
- 预留系统 `msgType = 0x0001`（`PING`）、`0x0002`（`PONG`），Body 为空
- `HeartbeatHandler` 监听 `IdleStateEvent`：`WRITER_IDLE` 时主动发送 `PING`；收到对端 `PING` 时回复 `PONG`
- `READER_IDLE` 或 `ALL_IDLE` 超时时 MUST 关闭 Channel（防止僵死连接）

### 5. TCP 与 WebSocket 服务端 API

**选择**：

- `TcpNettyServer`：封装 `ServerBootstrap`，绑定 `jackal.netty.tcp.port`
- `WebSocketNettyServer`：绑定 `jackal.netty.websocket.port`，路径 `jackal.netty.websocket.path`（默认 `/ws`）
- 两者实现 `SmartLifecycle` 或通过 `NettyServerLifecycle` 统一在 `ApplicationReadyEvent` 后启动

**理由**：与 Spring Boot 启动顺序一致，避免 Bean 未就绪时接受连接。

### 6. 扩展点

**选择**：

- `ChannelInitializerConfigurer` 函数式接口 / 抽象类，业务注册额外 Handler
- `NettyMessageHandler`：接收已解码的 `ProtocolFrame`，返回可选回复帧
- 使用 Spring `@ConditionalOnProperty` 分别控制 TCP、WS 启用

### 7. 配置前缀

**选择**：`jackal.netty`（与项目 artifact 名一致）

```yaml
jackal:
  netty:
    enabled: true
    tcp:
      enabled: true
      port: 9000
      boss-threads: 1
      worker-threads: 0  # 0 表示默认 CPU*2
    websocket:
      enabled: true
      port: 9001
      path: /ws
    protocol:
      max-frame-length: 1048576
    heartbeat:
      reader-idle-seconds: 90   # 读空闲：未收到任何数据则关闭
      writer-idle-seconds: 30   # 写空闲：发送 PING
      all-idle-seconds: 0       # 0 表示不启用 ALL_IDLE
```

### 8. 心跳与校验和（必选）

**选择**：
- **CRC32**：编解码器始终读写 4 字节校验和；计算范围为帧头 + Body（不含 checksum 字段本身）
- **`IdleStateHandler`**：三项超时均可配置；首版默认读 90s、写 30s
- **`HeartbeatHandler`**：与 `IdleStateHandler` 配对，不替代业务层心跳逻辑，仅保证链路存活探测

**理由**：长连接场景下僵死连接与静默断线常见；校验和降低传输差错导致的业务异常。

**备选**：应用层 JSON ping — 与二进制协议栈重复，故采用协议级 `msgType`。

## Risks / Trade-offs


| 风险                           | 缓解                                                    |
| ---------------------------- | ----------------------------------------------------- |
| Netty 与 Tomcat 线程模型并存，运维误配端口 | 文档明确默认端口；启动时日志打印绑定地址；配置校验                             |
| 半包/恶意超大帧导致 OOM               | `max-frame-length` 强制上限；解码器超长关闭连接                     |
| WebSocket 与 TCP 协议语义不一致      | 载荷统一为同一 `ProtocolFrame` 字节序列；WS 仅作传输载体                |
| Spring Boot 停服时连接未释放         | `SmartLifecycle.stop()` + `shutdownGracefully` + 超时配置 |
| 单模块膨胀                        | 包边界清晰，后续可无痛拆子模块                                       |


## Migration Plan

1. 合并代码后，默认 `jackal.netty.enabled=false`，避免影响现有仅 HTTP 部署
2. 需要长连接的环境在 `application-*.yml` 打开开关并放行端口
3. 回滚：关闭配置或移除依赖，无数据库迁移

## Open Questions

- 子模块拆分时机：当有第二个应用依赖时再拆 `jackal-netty`

