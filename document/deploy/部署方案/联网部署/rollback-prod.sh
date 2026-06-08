#!/bin/bash

set -Eeuo pipefail

# ================= 路径定位 =================
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ================= 读取配置 =================
# 如需调整配置，直接修改同目录下的 prod.env。
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/prod.env}"
if [ -f "$ENV_FILE" ]; then
    set -a
    . "$ENV_FILE"
    set +a
fi

# ================= 回滚参数 =================
ENV="${ENV:-prod}"
REPO_NAME="${REPO_NAME:-java-template}"
APP_PORT="${APP_PORT:-18080}"
APP_LOG_DIR="${APP_LOG_DIR:-../logs}"
APP_MEMORY_LIMIT="${APP_MEMORY_LIMIT:-1G}"
APP_MEMORY_RESERVATION="${APP_MEMORY_RESERVATION:-512M}"

TARGET_IMAGE="${1:-${APP_IMAGE:-}}"

# ================= 参数校验 =================
if [ -z "$TARGET_IMAGE" ]; then
    echo "请指定要回滚的镜像，例如："
    echo "  bash rollback-prod.sh ${REPO_NAME}:${ENV}-20260605170000-abcdef0"
    echo
    echo "当前本机可用的 ${REPO_NAME}:${ENV}-* 镜像："
    docker image ls "$REPO_NAME" --format "  {{.Repository}}:{{.Tag}}\t{{.CreatedSince}}\t{{.Size}}" \
        | awk -v env="$ENV" '$1 ~ ":" env "-" { print }'
    exit 1
fi

if ! docker image inspect "$TARGET_IMAGE" >/dev/null 2>&1; then
    echo "镜像不存在: $TARGET_IMAGE"
    echo "可用镜像："
    docker image ls "$REPO_NAME" --format "  {{.Repository}}:{{.Tag}}\t{{.CreatedSince}}\t{{.Size}}" \
        | awk -v env="$ENV" '$1 ~ ":" env "-" { print }'
    exit 1
fi

# ================= Compose 环境变量 =================
APP_CONTAINER_NAME="${APP_CONTAINER_NAME:-${REPO_NAME}-${ENV}}"
COMPOSE_PROJECT_NAME="${COMPOSE_PROJECT_NAME:-${REPO_NAME}-${ENV}}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-$ENV}"
APP_IMAGE="$TARGET_IMAGE"

export APP_CONTAINER_NAME COMPOSE_PROJECT_NAME SPRING_PROFILES_ACTIVE APP_PORT APP_LOG_DIR APP_MEMORY_LIMIT APP_MEMORY_RESERVATION APP_IMAGE

REPO_PATH="$(cd "$SCRIPT_DIR/.." && pwd)/$REPO_NAME"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
APP_BUILD_CONTEXT="$REPO_PATH"
APP_DOCKERFILE="$SCRIPT_DIR/Dockerfile"
export APP_BUILD_CONTEXT APP_DOCKERFILE

echo "进入仓库目录: $REPO_PATH"
cd "$REPO_PATH" || { echo "无法进入目录: $REPO_PATH"; exit 1; }

# ================= 运行用户与日志目录 =================
APP_UID="${SUDO_UID:-$(id -u)}"
APP_GID="${SUDO_GID:-$(id -g)}"
export APP_UID APP_GID

mkdir -p "$APP_LOG_DIR"
if [ "$(id -u)" -eq 0 ]; then
    chown "$APP_UID:$APP_GID" "$APP_LOG_DIR"
else
    echo "当前非 root 用户运行，跳过日志目录属主调整: $APP_LOG_DIR"
fi

# ================= 回滚启动 =================
echo "回滚到镜像: $APP_IMAGE"
docker compose -f "$COMPOSE_FILE" up -d --no-build app-server || { echo "Docker Compose 回滚启动失败"; exit 1; }

echo "等待容器状态..."
sleep 5

if ! docker ps --filter "name=^/${APP_CONTAINER_NAME}$" --filter "status=running" --format "{{.Names}}" | grep -qx "$APP_CONTAINER_NAME"; then
    echo "容器未处于运行状态，请查看日志："
    echo "  docker logs --tail=200 $APP_CONTAINER_NAME"
    exit 1
fi

echo "回滚完成: $APP_CONTAINER_NAME -> $APP_IMAGE"
