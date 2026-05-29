#!/bin/bash

# ================= 配置 =================
ENV="prod"
REPO_PATH="/home/jia/workspace/java-template"
BRANCH_NAME="main"
SERVICE="java-template"
IMAGE="${SERVICE}:${ENV}"

# ================= 执行 =================
echo "进入仓库目录: $REPO_PATH"
cd "$REPO_PATH" || { echo "❌ 无法进入目录: $REPO_PATH"; exit 1; }

echo "切换分支并拉取最新代码..."
git fetch origin || exit 1
if git show-ref --verify --quiet refs/heads/$BRANCH_NAME; then
    git checkout $BRANCH_NAME
else
    git checkout -b $BRANCH_NAME origin/$BRANCH_NAME
fi
git reset --hard origin/$BRANCH_NAME || { echo "❌ Git 拉取失败"; exit 1; }

echo "清除旧镜像: $IMAGE ..."
docker rmi -f "$IMAGE" 2>/dev/null

echo "构建并启动服务: $SERVICE ..."
docker compose -f docker-compose.yml -f docker-compose.${ENV}.yml up -d --build "$SERVICE" || { echo "❌ Docker Compose 启动失败"; exit 1; }

echo "✅ 部署完成: $SERVICE"
