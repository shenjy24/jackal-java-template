# java-template

Spring Boot 3 模板项目（无模块）。

## 技术栈

- Java 21
- Spring Boot 3.5.13
- MyBatis Plus 3.5.12
- MySQL 8.0
- Caffeine Cache
- Docker

## 快速开始

```bash
# 本地运行
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 打包
mvn package -DskipTests

# Docker 构建
docker build -t java-template:dev .
```
