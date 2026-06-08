#!/bin/bash

set -Eeuo pipefail

# ================= Path =================
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PACKAGE_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
cd "$PACKAGE_ROOT"

# ================= Load config =================
OFFLINE_ENV_FILE="${OFFLINE_ENV_FILE:-$SCRIPT_DIR/offline.env}"
if [ -f "$OFFLINE_ENV_FILE" ]; then
    set -a
    . "$OFFLINE_ENV_FILE"
    set +a
fi

ENV="${ENV:-prod}"
REPO_NAME="${REPO_NAME:-java-template}"
APP_PORT="${APP_PORT:-18080}"
APP_LOG_DIR="${APP_LOG_DIR:-/data/java-template/logs}"
APP_MEMORY_LIMIT="${APP_MEMORY_LIMIT:-1G}"
APP_MEMORY_RESERVATION="${APP_MEMORY_RESERVATION:-512M}"
APP_CONTAINER_NAME="${APP_CONTAINER_NAME:-${REPO_NAME}-${ENV}}"
COMPOSE_PROJECT_NAME="${COMPOSE_PROJECT_NAME:-${REPO_NAME}-${ENV}}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-$ENV}"
JAR_DIR="${JAR_DIR:-jars}"
JAR_FILE="${1:-${JAR_FILE:-}}"
RUNTIME_IMAGE="${RUNTIME_IMAGE:-eclipse-temurin:21-jre}"
DOCKERFILE_PATH="${DOCKERFILE_PATH:-$SCRIPT_DIR/Dockerfile}"

if [ -z "$JAR_FILE" ]; then
    mapfile -t JAR_FILES < <(find "$JAR_DIR" -maxdepth 1 -type f -name "*.jar" | sort)
    if [ "${#JAR_FILES[@]}" -eq 1 ]; then
        JAR_FILE="${JAR_FILES[0]}"
    else
        echo "请指定 jar 文件，例如："
        echo "  bash start-offline.sh jars/jackal-java-template.jar"
        echo
        echo "或在 offline.env 中配置："
        echo "  JAR_FILE=/data/java-template/jars/jackal-java-template.jar"
        exit 1
    fi
fi

if [ ! -f "$JAR_FILE" ]; then
    echo "jar 文件不存在: $JAR_FILE"
    exit 1
fi

if [ ! -f "$DOCKERFILE_PATH" ]; then
    echo "Dockerfile 不存在: $DOCKERFILE_PATH"
    exit 1
fi

if ! docker image inspect "$RUNTIME_IMAGE" >/dev/null 2>&1; then
    echo "运行时基础镜像不存在: $RUNTIME_IMAGE"
    echo "请先执行 load-images.sh 加载基础镜像。"
    exit 1
fi

BUILD_TIME="$(date +%Y%m%d%H%M%S)"
JAR_BASENAME="$(basename "$JAR_FILE" .jar)"
IMAGE_VERSION="${ENV}-${BUILD_TIME}-${JAR_BASENAME}"
APP_IMAGE="${APP_IMAGE:-${REPO_NAME}:${IMAGE_VERSION}}"

APP_UID="${SUDO_UID:-$(id -u)}"
APP_GID="${SUDO_GID:-$(id -g)}"
export APP_CONTAINER_NAME COMPOSE_PROJECT_NAME SPRING_PROFILES_ACTIVE APP_PORT APP_LOG_DIR APP_MEMORY_LIMIT APP_MEMORY_RESERVATION APP_IMAGE APP_UID APP_GID

# ================= Build app image =================
BUILD_CONTEXT="$(mktemp -d)"
cleanup() {
    rm -rf "$BUILD_CONTEXT"
}
trap cleanup EXIT

cp "$JAR_FILE" "$BUILD_CONTEXT/app.jar"
cp "$DOCKERFILE_PATH" "$BUILD_CONTEXT/Dockerfile"

echo "构建应用镜像: $APP_IMAGE"
docker build \
    --build-arg "APP_UID=$APP_UID" \
    --build-arg "APP_GID=$APP_GID" \
    --build-arg "JAR_FILE=app.jar" \
    -t "$APP_IMAGE" \
    "$BUILD_CONTEXT"

cat > "$SCRIPT_DIR/app-image.env" <<EOF
APP_IMAGE=$APP_IMAGE
JAR_FILE=$JAR_FILE
BUILD_TIME=$BUILD_TIME
EOF

echo "应用镜像构建完成: $APP_IMAGE"

# ================= Start app =================
mkdir -p "$APP_LOG_DIR"
if [ "$(id -u)" -eq 0 ]; then
    chown "$APP_UID:$APP_GID" "$APP_LOG_DIR"
else
    echo "当前非 root 用户运行，跳过日志目录属主调整: $APP_LOG_DIR"
fi

echo "启动应用: $APP_IMAGE"
docker compose -f docker-compose.yml up -d --no-build app-server

echo "等待容器状态..."
sleep 5

if ! docker ps --filter "name=^/${APP_CONTAINER_NAME}$" --filter "status=running" --format "{{.Names}}" | grep -qx "$APP_CONTAINER_NAME"; then
    echo "容器未处于运行状态，请查看日志："
    echo "  docker logs --tail=200 $APP_CONTAINER_NAME"
    exit 1
fi

echo "内网启动完成: $APP_CONTAINER_NAME -> $APP_IMAGE"
echo "检查应用:"
echo "  curl -I http://127.0.0.1:$APP_PORT"
