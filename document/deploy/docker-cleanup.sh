#!/bin/bash

##############################################
# Docker 构建缓存清理脚本（精简版）
# 用途：只清理构建缓存，不影响容器、镜像、网络
# 作者：Joe
# 日期：2025-09-29
##############################################

# 日志文件路径
LOG_FILE="/home/tech/workspace/docker/cleanup.log"

# 记录日志函数
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# 开始清理
log "=========================================="
log "开始 Docker 构建缓存清理任务"
log "=========================================="

# 显示清理前的磁盘使用情况
log "清理前的磁盘使用情况："
docker system df | tee -a "$LOG_FILE"

# 清理构建缓存（全部清理，不保留任何缓存）
log ""
log ">>> 清理所有构建缓存"
BUILD_CACHE=$(docker builder prune -a -f 2>&1)
log "$BUILD_CACHE"

# 显示清理后的磁盘使用情况
log ""
log "清理后的磁盘使用情况："
docker system df | tee -a "$LOG_FILE"

# 计算释放的空间
log ""
log "=========================================="
log "Docker 构建缓存清理任务完成"
log "=========================================="

# 保持日志文件大小在合理范围内（保留最近1000行）
if [ -f "$LOG_FILE" ]; then
    tail -n 1000 "$LOG_FILE" > "$LOG_FILE.tmp" && mv "$LOG_FILE.tmp" "$LOG_FILE"
fi

exit 0