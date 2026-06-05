#!/bin/bash

# 开启严格模式：遇到错误、未定义变量或管道错误立即退出
set -Eeuo pipefail

# ================= 部署参数 =================
# 生产环境固定参数；如需调整端口、仓库名或分支，只改本段即可。
ENV="prod"
BRANCH_NAME="main"
REPO_NAME="java-template"
APP_PORT="18080"
APP_LOG_DIR="../logs"

# ================= Compose 环境变量 =================
# docker-compose.yml 通过这些变量生成镜像名、容器名、项目名和 Spring Profile。
APP_IMAGE="${REPO_NAME}:${ENV}"
APP_CONTAINER_NAME="${REPO_NAME}-${ENV}"
COMPOSE_PROJECT_NAME="${REPO_NAME}-${ENV}"
SPRING_PROFILES_ACTIVE="$ENV"

export APP_IMAGE APP_CONTAINER_NAME COMPOSE_PROJECT_NAME SPRING_PROFILES_ACTIVE APP_PORT APP_LOG_DIR

# ================= 路径定位 =================
# 脚本放在部署目录下，目标仓库目录与脚本目录同级。
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_PATH="$SCRIPT_DIR/$REPO_NAME"

# ================= 执行 =================
echo "进入仓库目录: $REPO_PATH"
cd "$REPO_PATH" || { echo "无法进入目录: $REPO_PATH"; exit 1; }

# 同步远程分支：本地存在则切换，本地不存在则基于 origin 创建。
echo "切换分支并拉取最新代码..."
git fetch --prune origin || exit 1
if git show-ref --verify --quiet refs/heads/$BRANCH_NAME; then
    git checkout $BRANCH_NAME
else
    git checkout -b $BRANCH_NAME origin/$BRANCH_NAME
fi

# 确保本地代码库与远程完全一致，避免旧文件残留影响生产部署。
git reset --hard origin/$BRANCH_NAME || { echo "Git 拉取失败"; exit 1; }

# ================= 运行用户与日志目录 =================
# 绑定宿主机用户；兼容 sudo 执行，避免容器写出的日志归属 root。
APP_UID="${SUDO_UID:-$(id -u)}"
APP_GID="${SUDO_GID:-$(id -g)}"
export APP_UID APP_GID

# 非 root 用户无法 chown 时只创建目录，避免部署流程被属主调整打断。
mkdir -p "$APP_LOG_DIR"
if [ "$(id -u)" -eq 0 ]; then
    chown "$APP_UID:$APP_GID" "$APP_LOG_DIR"
else
    echo "当前非 root 用户运行，跳过日志目录属主调整: $APP_LOG_DIR"
fi

# ================= 构建与启动 =================
echo "构建并启动服务..."
docker compose -f docker-compose.yml up -d --build || { echo "Docker Compose 启动失败"; exit 1; }

# 清理构建后遗留的 dangling image，降低服务器磁盘压力。
echo "清除无用的悬空镜像..."
docker image prune -f

echo "部署完成: $APP_CONTAINER_NAME"
