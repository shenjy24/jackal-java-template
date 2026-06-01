## ADDED Requirements

### Requirement: 默认帧头字段

默认二进制协议实现 MUST 包含以下帧头字段（大端序）：2 字节魔数 `0x5A5A`、1 字节版本、2 字节消息类型、1 字节标志、4 字节序号、4 字节 Body 长度，随后为变长 Body；帧尾 MUST 附加 4 字节 CRC32（覆盖 magic 至 body，不含 checksum 字段本身）。

#### Scenario: 编码合法帧

- **WHEN** 业务构造带 Body 的 `ProtocolFrame` 且各字段在允许范围内
- **THEN** 编码器输出符合上述布局的字节序列

#### Scenario: 解码合法帧

- **WHEN** 输入缓冲区包含完整一帧且魔数、版本、长度合法
- **THEN** 解码器输出一个 `ProtocolFrame` 对象并从缓冲区消费对应字节

### Requirement: 粘包与半包处理

解码器 MUST 正确处理 TCP 粘包与半包，仅在收齐一整帧后才向后续 Handler 传递消息。

#### Scenario: 半包到达

- **WHEN** 仅收到帧头或部分 Body
- **THEN** 解码器不得向上游传递不完整 `ProtocolFrame`，并等待后续字节

#### Scenario: 多帧粘包

- **WHEN** 一次读事件包含多帧连续字节
- **THEN** 解码器 MUST 按顺序解码并传递多个 `ProtocolFrame`

### Requirement: 最大帧长限制

系统 MUST 拒绝 Body 长度超过 `jackal.netty.protocol.max-frame-length` 的帧，并关闭或重置连接。

#### Scenario: 超长 Body 声明

- **WHEN** 帧头中 bodyLength 大于配置上限
- **THEN** 解码失败，Channel 被关闭且记录错误日志

### Requirement: 协议可替换扩展

系统 MUST 通过 `ProtocolCodec`（或等价接口）抽象编解码，使默认实现可被自定义实现替换，且 TCP 与 WebSocket 共用同一 Codec 实例或工厂。

#### Scenario: 自定义 Codec Bean

- **WHEN** 应用提供 `ProtocolCodec` 类型的 Spring Bean
- **THEN** TCP 与 WebSocket Pipeline 使用该实现进行编解码，而非默认实现

### Requirement: 必选 CRC32 校验和

编码器 MUST 对每一帧写入 CRC32；解码器 MUST 校验每一帧的 CRC32，失败时 MUST 丢弃帧并关闭连接。

#### Scenario: 编码含校验和

- **WHEN** 业务构造任意 `ProtocolFrame` 并编码
- **THEN** 输出字节序列帧尾包含 4 字节 CRC32，且与帧头+Body 计算值一致

#### Scenario: 校验失败

- **WHEN** 帧尾 CRC 与对 magic 至 body 的计算值不一致
- **THEN** 不得将帧交给业务 Handler，并关闭 Channel
