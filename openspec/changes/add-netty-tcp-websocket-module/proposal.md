## Why

当前模板项目仅提供基于 Spring MVC 的 HTTP 能力，缺少面向长连接、实时推送或设备接入等场景的底层网络基础设施。引入基于 Netty 的可复用基础模块，可统一 TCP 与 WebSocket 的启动、编解码与生命周期管理，并为自定义二进制协议提供稳定扩展点，避免业务侧重复搭建 Netty 样板代码。

## What Changes

- 新增独立 Maven 模块（或清晰划分的 `netty` 包结构），封装 Netty 公共能力：事件循环、Channel 配置、优雅关闭、可配置端口等
- 提供 **TCP 服务端** 启动器与 Pipeline 骨架，支持接入自定义二进制协议编解码器
- 提供 **WebSocket 服务端** 启动器，在握手完成后复用同一套二进制帧编解码（或协议 Handler 链）
- 定义 **自定义二进制协议** 最小规范：帧头（魔数、版本、长度、消息类型等）、Body 载荷、**帧尾 CRC32 校验和（必选）**；提供默认 `ByteToMessageDecoder` / `MessageToByteEncoder` 实现
- 在 TCP 与 WebSocket Pipeline 中内置 **`IdleStateHandler` 心跳**：可配置读/写/全空闲超时，超时后发送协议级心跳帧或关闭僵死连接
- 提供 Spring Boot 自动配置（`application.yml` 开关与端口配置），与主应用生命周期对齐（`SmartLifecycle` 或 `@PreDestroy` 优雅停机）
- 提供示例 Handler 与集成测试/本地启动说明（文档或 `readme` 片段），便于业务快速接入

## Capabilities

### New Capabilities

- `netty-core`: Netty 公共基础设施（线程模型、ServerBootstrap 抽象、配置属性、生命周期、异常与日志约定）
- `netty-tcp-server`: TCP 服务端启动、Pipeline 扩展点、连接管理
- `netty-websocket-server`: WebSocket 服务端（HTTP 升级、帧处理）及与二进制协议层的衔接
- `netty-binary-protocol`: 自定义二进制帧格式、编解码器接口与默认实现

### Modified Capabilities

（无：项目尚无既有 OpenSpec 能力规格）

## Impact

- **依赖**：根 `pom.xml` 或新子模块引入 `io.netty:netty-all`（或按需拆分 `netty-handler`、`netty-codec-http` 等）；可能与现有 Spring Boot Web 端口并存，需避免端口冲突
- **代码结构**：建议 `com.tech.netty`（或 `jackal-netty` 子模块）下分 `core`、`tcp`、`websocket`、`protocol`、`config` 包
- **配置**：新增 `jackal.netty.*`（或 `app.netty.*`）配置项；`application-*.yml` 需补充示例
- **运维**：独立 TCP/WebSocket 端口需在 Docker/nginx 与防火墙规则中放行；与现有 HTTP 服务部署文档需同步说明
- **非目标（本变更不涵盖）**：客户端 SDK、TLS/mTLS、集群会话粘性、具体业务消息语义（仅提供协议扩展点；系统级心跳 `msgType` 由模块预留，业务类型从业务枚举段分配）
