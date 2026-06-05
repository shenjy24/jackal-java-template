#!/bin/bash

# 开启严格模式：遇到错误、未定义变量或管道错误立即退出
set -Eeuo pipefail

# ================= 配置 =================
ENV="prod"
BRANCH_NAME="main"
REPO_NAME="jackal-java-template"
SERVICE="java-template"
IMAGE="${SERVICE}:${ENV}"

# 获取脚本所在路径
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_PATH="$SCRIPT_DIR/$REPO_NAME"

# ================= 执行 =================
echo "进入仓库目录: $REPO_PATH"
cd "$REPO_PATH" || { echo "无法进入目录: $REPO_PATH"; exit 1; }

echo "切换分支并拉取最新代码..."
git fetch --prune origin || exit 1
if git show-ref --verify --quiet refs/heads/$BRANCH_NAME; then
    git checkout $BRANCH_NAME
else
    git checkout -b $BRANCH_NAME origin/$BRANCH_NAME
fi

# 确保本地代码库与远程完全一致
git reset --hard origin/$BRANCH_NAME || { echo "Git 拉取失败"; exit 1; }

echo "构建并启动服务: $SERVICE ..."
docker compose -f docker-compose.yml -f docker-compose.${ENV}.yml up -d --build "$SERVICE" || { echo "Docker Compose 启动失败"; exit 1; }

echo "清除无用的悬空镜像..."
docker image prune -f

echo "部署完成: $SERVICE"
