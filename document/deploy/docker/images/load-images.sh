#!/bin/bash

set -Eeuo pipefail

# ================= Path =================
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PACKAGE_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
cd "$PACKAGE_ROOT"

# ================= Load config =================
IMAGES_ENV_FILE="${IMAGES_ENV_FILE:-$SCRIPT_DIR/images.env}"

if [ -f "$IMAGES_ENV_FILE" ]; then
    set -a
    . "$IMAGES_ENV_FILE"
    set +a
fi

IMAGE_DIR="${IMAGE_DIR:-images}"
MYSQL_IMAGE="${MYSQL_IMAGE:-mysql:8.4}"
MYSQL_IMAGE_TAR="${MYSQL_IMAGE_TAR:-$IMAGE_DIR/mysql-8.4.tar}"
LOAD_RUNTIME_IMAGE="${LOAD_RUNTIME_IMAGE:-1}"
RUNTIME_IMAGE="${RUNTIME_IMAGE:-eclipse-temurin:21-jre}"
RUNTIME_IMAGE_TAR="${RUNTIME_IMAGE_TAR:-$IMAGE_DIR/eclipse-temurin-21-jre.tar}"
LOAD_NGINX_IMAGE="${LOAD_NGINX_IMAGE:-0}"
NGINX_IMAGE="${NGINX_IMAGE:-nginx:1.30.2}"
NGINX_IMAGE_TAR="${NGINX_IMAGE_TAR:-$IMAGE_DIR/nginx-1.30.2.tar}"

load_image() {
    local image_tar="$1"
    local label="$2"

    if [ ! -f "$image_tar" ]; then
        echo "未找到${label}镜像文件: $image_tar"
        exit 1
    fi

    echo "加载${label}镜像: $image_tar"
    docker load -i "$image_tar"
}

load_image "$MYSQL_IMAGE_TAR" "MySQL"

if [ "$LOAD_RUNTIME_IMAGE" = "1" ]; then
    load_image "$RUNTIME_IMAGE_TAR" "Java 运行时"
else
    echo "跳过 Java 运行时镜像加载，LOAD_RUNTIME_IMAGE=$LOAD_RUNTIME_IMAGE"
fi

if [ "$LOAD_NGINX_IMAGE" = "1" ]; then
    load_image "$NGINX_IMAGE_TAR" "Nginx"
else
    echo "跳过 Nginx 镜像加载，LOAD_NGINX_IMAGE=$LOAD_NGINX_IMAGE"
fi

if ! docker image inspect "$MYSQL_IMAGE" >/dev/null 2>&1; then
    echo "MySQL 镜像未加载成功或镜像 tag 不匹配: $MYSQL_IMAGE"
    exit 1
fi

if [ "$LOAD_RUNTIME_IMAGE" = "1" ] && ! docker image inspect "$RUNTIME_IMAGE" >/dev/null 2>&1; then
    echo "Java 运行时镜像未加载成功或镜像 tag 不匹配: $RUNTIME_IMAGE"
    exit 1
fi

if [ "$LOAD_NGINX_IMAGE" = "1" ] && ! docker image inspect "$NGINX_IMAGE" >/dev/null 2>&1; then
    echo "Nginx 镜像未加载成功或镜像 tag 不匹配: $NGINX_IMAGE"
    exit 1
fi

echo "镜像加载完成。"
