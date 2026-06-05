# java-template

Spring Boot 3 后端服务，默认端口 **8080**。

## 环境

| Profile | 用途 | 数据库 | 端口   |
|---------|------|--------|------|
| `local` | 本地开发 | `template` | 8080 |
| `dev`   | 测试环境 | `template_dev` | 8080 |
| `prod`  | 生产环境 | `template_prod` | 8080 |

配置文件：`src/main/resources/application.yml`（公共）+ `application-{profile}.yml`（环境差异）。

**上线前必改**：数据库连接、阿里云 OSS 密钥（`aliyun.*`）。

## 部署

### 生产发布

```bash
# 一键部署（拉代码 → 构建镜像 → 启动）
bash document/deploy/start-prod.sh prod
```

脚本逻辑：拉取 `main` 分支 → 按参数导出环境变量 → `docker compose -f docker-compose.yml up -d --build`

### 手动部署

```bash
# 构建镜像
docker build -t jackal-java-template-app-server:prod .

# 启动（dev / prod 二选一）
bash document/deploy/start-prod.sh dev
bash document/deploy/start-prod.sh prod
```

| 环境 | 容器名 | 镜像 | 宿主机端口 |
|------|--------|------|-----------|
| prod | `jackal-java-template-app-server-prod` | `jackal-java-template-app-server:prod` | 18080 |
| dev  | `jackal-java-template-app-server-dev`  | `jackal-java-template-app-server:dev`  | 28080 |

容器限制：内存上限 1G，日志轮转 20MB × 5 份。

### 日志挂载

dev/prod 挂载宿主机目录到容器 `/app/logs`，可通过 `APP_LOG_DIR` 覆盖：

```
/home/jia/workspace/java-template/logs → /app/logs
```

## 数据库

初始化脚本：`document/sql/线上版本/`

- `user.sql` — 用户相关表
- `auth.sql` — 权限相关表

## 日志

| 类型 | 路径 | 保留 |
|------|------|------|
| 应用日志 | `logs/app/{日期}/app_log-*.log` | 30 天，单文件 100MB，总量 3GB |
| 错误日志 | `logs/error/{日期}/error_log-*.log` | 90 天，总量 5GB |

查看容器日志：

```bash
docker logs -f jackal-java-template-app-server-prod
```

## Nginx

生产配置参考：`document/deploy/nginx/nginx-prod.conf`

- `/api/` → 后端 `127.0.0.1:8000`
- `/` → 前端 `127.0.0.1:30000`

## 常用命令

```bash
# 重启
docker compose -f docker-compose.yml restart app-server

# 停止
docker compose -f docker-compose.yml down

# 清理 Docker 构建缓存
bash document/deploy/docker-cleanup.sh

# 本地运行（开发调试）
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
