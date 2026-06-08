# Docker 清理文档

本文档说明如何使用 `docker-cleanup.sh` 清理 Docker 构建缓存。

## 脚本定位

`docker-cleanup.sh` 只负责清理 Docker build cache，用于降低服务器长期构建后的磁盘占用。

脚本不会清理：

- 运行中或已停止的容器
- 业务镜像
- Docker 网络
- Docker volume

业务镜像保留策略由部署脚本负责，联网部署脚本会按 `IMAGE_KEEP_COUNT` 保留最近几个业务镜像。

## 默认策略

默认只清理超过 7 天的构建缓存：

```bash
docker builder prune -a -f --filter until=168h
```

相比每次清空全部 build cache，这种方式可以保留近期构建缓存，减少后续部署构建时间。

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `LOG_FILE` | 脚本同目录 `cleanup.log` | 清理日志路径 |
| `LOCK_FILE` | `/tmp/docker-cleanup.lock` | 并发锁文件 |
| `PRUNE_UNTIL` | `168h` | 清理多久以前的构建缓存 |
| `PRUNE_ALL` | `0` | 设置为 `1` 时清理全部构建缓存 |
| `LOG_KEEP_LINES` | `1000` | 日志保留行数 |

## 手动执行

进入项目根目录后执行：

```bash
bash document/deploy/docker/docker-cleanup.sh
```

清理 30 天以前的构建缓存：

```bash
PRUNE_UNTIL=720h bash document/deploy/docker/docker-cleanup.sh
```

清理全部构建缓存：

```bash
PRUNE_ALL=1 bash document/deploy/docker/docker-cleanup.sh
```

指定日志路径：

```bash
LOG_FILE=/data/logs/docker-cleanup.log bash document/deploy/docker/docker-cleanup.sh
```

## 定时任务

建议每周执行一次。编辑定时任务：

```bash
crontab -e
```

添加配置：

```cron
0 3 * * 0 cd /home/tech/workspace/java-template && /bin/bash document/deploy/docker/docker-cleanup.sh >/dev/null 2>&1
```

请根据服务器实际项目路径替换：

```text
/home/tech/workspace/java-template
```

查看日志：

```bash
tail -f document/deploy/docker/cleanup.log
```

## 注意事项

- 不建议在每次部署后自动执行该脚本，否则会降低 Docker 构建缓存命中率。
- 磁盘紧张时可临时使用 `PRUNE_ALL=1`，但下次构建会重新下载依赖和构建缓存。
- 如提示无 Docker 权限，请确认当前用户已加入 `docker` 组，或使用具备 Docker 权限的用户执行。
- 如果脚本提示 lock 文件存在，说明已有清理任务正在执行；确认没有任务运行后再处理 lock 文件。
