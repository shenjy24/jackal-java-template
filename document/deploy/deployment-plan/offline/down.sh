#!/bin/bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

resolve_deploy_path() {
    case "$1" in
        /*) printf '%s\n' "$1" ;;
        *) printf '%s\n' "$SCRIPT_DIR/$1" ;;
    esac
}

ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/app.env}"
if [ -f "$ENV_FILE" ]; then
    set -a
    . "$ENV_FILE"
    set +a
fi

ENV="${ENV:-prod}"
REPO_NAME="${REPO_NAME:-java-template}"
APP_PORT="${APP_PORT:-18080}"
APP_LOG_DIR="$(resolve_deploy_path "${APP_LOG_DIR:-../logs}")"
APP_MEMORY_LIMIT="${APP_MEMORY_LIMIT:-1G}"
APP_MEMORY_RESERVATION="${APP_MEMORY_RESERVATION:-512M}"
APP_CONTAINER_NAME="${APP_CONTAINER_NAME:-${REPO_NAME}-${ENV}}"
COMPOSE_PROJECT_NAME="${COMPOSE_PROJECT_NAME:-${REPO_NAME}-${ENV}}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-$ENV}"
COMPOSE_FILE="${COMPOSE_FILE:-$SCRIPT_DIR/docker-compose.yml}"

export APP_CONTAINER_NAME COMPOSE_PROJECT_NAME SPRING_PROFILES_ACTIVE APP_PORT APP_LOG_DIR APP_MEMORY_LIMIT APP_MEMORY_RESERVATION

if ! command -v docker >/dev/null 2>&1; then
    echo "未安装 Docker，请先安装 Docker"
    exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
    echo "未检测到 Docker Compose v2，请先安装或升级 Docker Compose"
    exit 1
fi

echo "停止应用: $APP_CONTAINER_NAME"
docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" down || { echo "Docker Compose 停止失败"; exit 1; }
echo "停止完成: $APP_CONTAINER_NAME"
