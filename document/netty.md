# Netty TCP / WebSocket 模块

## 启用

在 `application-*.yml` 中设置：

```yaml
jackal:
  netty:
    enabled: true
```

- TCP 默认端口：`9000`
- WebSocket 默认端口：`9001`，路径：`/ws`

## 二进制帧格式（大端）

| 字段 | 长度 |
|------|------|
| magic | 2B (`0x5A5A`) |
| version | 1B (`0x01`) |
| msgType | 2B |
| flags | 1B |
| seqId | 4B |
| bodyLength | 4B |
| body | N |
| checksum | 4B CRC32（覆盖 magic 至 body） |

## 系统消息

| msgType | 含义 |
|---------|------|
| `0x0001` | PING |
| `0x0002` | PONG |

写空闲时服务端发送 PING；读空闲超时关闭连接。PING/PONG 不进入业务 Handler。

## 自定义业务 Handler

实现 `NettyMessageHandler` 并注册为 Spring Bean（替换或配合 `EchoProtocolHandler`）。

可选实现 `ChannelInitializerConfigurer` 向 Pipeline 尾部追加 Handler。

## 验证

1. 启用配置后启动应用，日志应出现端口绑定信息。
2. 使用 TCP 客户端发送带正确 CRC32 的完整帧，默认 `EchoProtocolHandler` 会回显。
3. WebSocket 使用二进制帧，载荷为完整协议字节（含 CRC32）。
