#!/bin/bash

set -Eeuo pipefail

##############################################
# Docker 构建缓存清理脚本
# 用途：只清理 build cache，不删除容器、镜像、网络和 volume
##############################################

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

LOG_FILE="${LOG_FILE:-$SCRIPT_DIR/cleanup.log}"
LOCK_FILE="${LOCK_FILE:-/tmp/docker-cleanup.lock}"
PRUNE_UNTIL="${PRUNE_UNTIL:-168h}"
PRUNE_ALL="${PRUNE_ALL:-0}"
LOG_KEEP_LINES="${LOG_KEEP_LINES:-1000}"

mkdir -p "$(dirname "$LOG_FILE")"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

finish() {
    rm -f "$LOCK_FILE"
}

fail() {
    log "清理失败: $1"
    exit 1
}

if [ -e "$LOCK_FILE" ]; then
    echo "Docker 清理任务已在执行，lock 文件: $LOCK_FILE"
    exit 1
fi

touch "$LOCK_FILE"
trap finish EXIT

if ! command -v docker >/dev/null 2>&1; then
    fail "未找到 docker 命令"
fi

if ! docker system df >/dev/null 2>&1; then
    fail "当前用户无法执行 docker 命令，请检查 Docker 服务或用户权限"
fi

log "=========================================="
log "开始 Docker 构建缓存清理任务"
log "日志文件: $LOG_FILE"
log "=========================================="

log "清理前的磁盘使用情况："
docker system df | tee -a "$LOG_FILE"

log ""
if [ "$PRUNE_ALL" = "1" ]; then
    log ">>> 清理所有构建缓存"
    docker builder prune -a -f 2>&1 | tee -a "$LOG_FILE"
else
    log ">>> 清理超过 $PRUNE_UNTIL 的构建缓存"
    docker builder prune -a -f --filter "until=$PRUNE_UNTIL" 2>&1 | tee -a "$LOG_FILE"
fi

log ""
log "清理后的磁盘使用情况："
docker system df | tee -a "$LOG_FILE"

log ""
log "=========================================="
log "Docker 构建缓存清理任务完成"
log "=========================================="

if [ -f "$LOG_FILE" ]; then
    tail -n "$LOG_KEEP_LINES" "$LOG_FILE" > "$LOG_FILE.tmp" && mv "$LOG_FILE.tmp" "$LOG_FILE"
fi

exit 0
