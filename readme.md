# java-template

Java 21 + Spring Boot 3 后端模板项目，默认本地端口 **8080**。

项目内置用户、权限、统一响应、异常处理、MyBatis Plus、日志配置、Docker 构建和部署参考文档，适合作为后台服务的基础工程。

## 技术栈

| 类型 | 说明 |
|------|------|
| JDK | Java 21 |
| Web 框架 | Spring Boot 3 |
| 构建工具 | Maven |
| 持久层 | MyBatis Plus |
| 数据库 | MySQL |
| 容器化 | Docker、Docker Compose |

## 环境

| Profile | 用途 | 数据库 | 端口 |
|---------|------|--------|------|
| `local` | 本地开发 | `template` | 8080 |
| `dev` | 测试环境 | `template_dev` | 8080 |
| `prod` | 生产环境 | `template_prod` | 8080 |

配置文件：

```text
src/main/resources/application.yml
src/main/resources/application-local.yml
src/main/resources/application-dev.yml
src/main/resources/application-prod.yml
```

上线前请检查并替换生产环境中的数据库连接、OSS 密钥等敏感配置。

## 项目结构

```text
.
├── src/
│   ├── main/
│   │   ├── java/com/tech/
│   │   │   ├── common/          # 通用注解、常量、枚举等
│   │   │   ├── component/       # 组件封装，如 OSS、缓存等
│   │   │   ├── config/          # Spring、MyBatis、响应、拦截器等配置
│   │   │   ├── controller/      # HTTP 接口入口，区分 admin 和 web
│   │   │   ├── repository/      # DAO、Entity、Mapper、BO/QO/VO
│   │   │   ├── service/         # 业务逻辑层
│   │   │   └── util/            # 工具类
│   │   └── resources/
│   │       ├── mapper/          # MyBatis XML
│   │       ├── application*.yml # 环境配置
│   │       └── logback-spring.xml
│   └── test/                    # 测试代码目录
├── document/
│   ├── deploy/                  # 部署脚本、部署方案和中间件配置
│   └── sql/线上版本/             # 初始化 SQL
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── readme.md
```

## 分层说明

| 目录 | 职责 |
|------|------|
| `controller/admin` | 后台管理接口 |
| `controller/web` | Web 端接口 |
| `service` | 业务逻辑、查询、命令和装配逻辑 |
| `repository/dao` | 数据访问封装 |
| `repository/entity` | 数据库实体 |
| `repository/mapper` | MyBatis Mapper 接口 |
| `repository/model/qo` | Controller 入参对象 |
| `repository/model/bo` | 业务过程对象 |
| `repository/model/vo` | 接口响应视图对象 |
| `config/response` | 统一响应包装 |
| `common` | 通用常量、枚举、注解 |

## 本地开发

启动本地环境：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

编译检查：

```bash
mvn -q -DskipTests compile
```

本地调试前请先确认：

- MySQL 已创建对应数据库。
- `application-local.yml` 中数据库连接可用。
- 如接口依赖 OSS、缓存等外部服务，需要补齐本地可用配置。

## 数据库脚本

初始化 SQL 位于：

```text
document/sql/线上版本/
├── ddl/    # 表结构
└── dml/    # 初始化数据
```

## 部署

部署流程、服务器准备、Docker、MySQL、Nginx、联网部署和回滚说明统一维护在：

```text
document/deploy/
```

部署方案入口：

```text
document/deploy/部署方案/
├── 内网部署/
└── 联网部署/
```

联网部署可参考：

```text
document/deploy/部署方案/联网部署/联网部署文档.md
```

## 常用文档

| 文档 | 说明 |
|------|------|
| `document/deploy/software/软件安装.md` | 基础软件安装 |
| `document/deploy/mysql/MySQL部署文档.md` | MySQL Docker 部署和备份 |
| `document/deploy/nginx/` | Nginx 配置参考 |
| `document/deploy/docker/Docker清理文档.md` | Docker 构建缓存清理 |
| `document/deploy/部署方案/联网部署/联网部署文档.md` | 联网部署和回滚 |
