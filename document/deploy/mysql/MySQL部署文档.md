# MySQL 部署文档

本文档说明如何使用 `docker-compose-mysql.yml` 部署 MySQL，以及如何使用 `mysql-backup.sh` 手动备份和配置定时备份。

## 目录说明

```text
document/deploy/mysql/
├── docker-compose-mysql.yml    # MySQL Docker Compose 配置
├── mysql-backup.sh             # MySQL 备份脚本
├── mysql/
│   ├── conf.d/                 # 自定义 MySQL 配置
│   ├── init/                   # 首次初始化 SQL
│   └── logs/                   # MySQL 日志挂载目录
└── backup/                     # 备份文件输出目录
```

`mysql-data` 使用 Docker volume 持久化，容器删除后数据不会随容器一起删除。

## 部署 MySQL

进入部署目录：

```bash
cd document/deploy/mysql
```

如需覆盖默认配置，可在当前目录新增 `.env` 文件：

```env
MYSQL_PORT=3306
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=template
```

变量说明：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MYSQL_PORT` | `3306` | 宿主机映射端口 |
| `MYSQL_ROOT_PASSWORD` | `123456` | root 用户密码 |
| `MYSQL_DATABASE` | `template` | 首次启动时自动创建的数据库 |

启动 MySQL：

```bash
docker compose -f docker-compose-mysql.yml up -d
```

查看容器状态：

```bash
docker ps --filter name=mysql
docker compose -f docker-compose-mysql.yml ps
```

查看日志：

```bash
docker logs -f mysql
```

停止服务：

```bash
docker compose -f docker-compose-mysql.yml down
```

如需连同数据卷一起删除，执行前请确认已经备份：

```bash
docker compose -f docker-compose-mysql.yml down -v
```

## 初始化 SQL

MySQL 官方镜像只会在首次创建数据目录时执行 `/docker-entrypoint-initdb.d` 下的脚本。

本项目已将以下目录挂载到初始化目录：

```text
./mysql/init:/docker-entrypoint-initdb.d
```

如果需要初始化表结构或基础数据，可将 SQL 文件放到：

```text
document/deploy/mysql/mysql/init/
```

首次启动容器时会按文件名顺序执行。已经初始化过的 `mysql-data` 数据卷不会重复执行初始化脚本。

## 手动备份

备份脚本：`mysql-backup.sh`

脚本默认配置：

| 配置 | 默认值 | 说明 |
|------|--------|------|
| `CONTAINER_NAME` | `mysql` | MySQL 容器名 |
| `DB_USER` | `root` | 备份账号 |
| `DB_PASS` | `123456` | 备份账号密码 |
| `DB_TARGET` | `--databases template` | 备份目标数据库 |
| `BACKUP_DIR` | `./backup` | 备份目录 |
| `RETENTION_DAYS` | `7` | 备份保留天数 |

执行备份：

```bash
cd document/deploy/mysql
chmod +x mysql-backup.sh
./mysql-backup.sh
```

备份成功后会生成压缩文件：

```text
backup/mysql/mysql_backup_yyyyMMdd_HHmmss.sql.gz
```

备份日志：

```text
backup/mysql/backup.log
```

脚本会在备份完成后自动清理超过 `RETENTION_DAYS` 天的旧备份。

## 定时备份

建议使用 Linux `crontab` 配置定时任务。以下示例每天凌晨 02:30 执行一次备份。

编辑定时任务：

```bash
crontab -e
```

添加配置：

```cron
30 2 * * * cd /home/jia/workspace/java-template/document/deploy/mysql && /bin/bash mysql-backup.sh >> /dev/null 2>&1
```

请根据服务器实际项目路径替换：

```text
/home/jia/workspace/java-template
```

查看当前定时任务：

```bash
crontab -l
```

查看定时任务执行日志：

```bash
tail -f document/deploy/mysql/backup/backup.log
```

## 恢复备份

先将备份文件解压并导入容器：

```bash
cd document/deploy/mysql
gzip -dc backup/mysql/mysql_backup_yyyyMMdd_HHmmss.sql.gz | docker exec -i mysql mysql -uroot -p123456
```

如果修改过 root 密码，请将命令中的 `123456` 替换为实际密码。

## 常用命令

```bash
# 启动
docker compose -f docker-compose-mysql.yml up -d

# 重启
docker compose -f docker-compose-mysql.yml restart mysql-service

# 停止
docker compose -f docker-compose-mysql.yml down

# 进入 MySQL 命令行
docker exec -it mysql mysql -uroot -p

# 查看数据卷
docker volume ls | grep mysql-data

# 手动备份
./mysql-backup.sh
```

## 注意事项

- 生产环境必须修改 `MYSQL_ROOT_PASSWORD`，并同步更新 `mysql-backup.sh` 中的 `DB_PASS`。
- 应用配置中的数据库名需要与 `MYSQL_DATABASE` 或初始化 SQL 创建的数据库保持一致。
- `docker compose down -v` 会删除 `mysql-data` 数据卷，执行前务必确认已有可用备份。
- 备份目录默认在项目目录下，建议定期同步到对象存储或其他服务器，避免单机故障导致数据和备份同时丢失。
