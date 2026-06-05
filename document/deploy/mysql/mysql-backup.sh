#!/bin/bash
set -eo pipefail

# ------------------- 配置区域 -------------------
CONTAINER_NAME="mysql"
DB_USER="root"
DB_PASS="123456"
DB_TARGET="--databases template"   # 明确指定业务库，避免备份系统库
BACKUP_DIR="./backup"              # 备份目录
RETENTION_DAYS=7                   # 保留天数

# ------------------- 运行变量 -------------------
DATE_SUFFIX=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/mysql_backup_${DATE_SUFFIX}.sql.gz"
LOG_FILE="${BACKUP_DIR}/backup.log"

# ------------------- 辅助函数 -------------------
log_info()  { echo "[$(date +'%Y-%m-%d %H:%M:%S')] [INFO]  $1" | tee -a "${LOG_FILE}"; }
log_error() { echo "[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR] $1" | tee -a "${LOG_FILE}"; }

# ------------------- 前置检查 -------------------
mkdir -p "${BACKUP_DIR}"

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    log_error "容器 ${CONTAINER_NAME} 未运行，备份终止"
    exit 1
fi

# ------------------- 执行备份 -------------------
log_info "--------------------------------------------------"
log_info "开始备份，目标文件: ${BACKUP_FILE}"

if docker exec \
    -e MYSQL_PWD="${DB_PASS}" \
    -i "${CONTAINER_NAME}" \
    mysqldump \
        -u"${DB_USER}" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        ${DB_TARGET} \
    | gzip > "${BACKUP_FILE}"; then

    # 校验压缩文件完整性
    if ! gzip -t "${BACKUP_FILE}" 2>/dev/null; then
        log_error "文件校验失败，备份可能已损坏"
        rm -f "${BACKUP_FILE}"
        exit 1
    fi

    FILE_SIZE=$(du -h "${BACKUP_FILE}" | awk '{print $1}')
    log_info "备份成功，文件大小: ${FILE_SIZE}"
else
    log_error "备份失败，请检查容器状态或账号密码"
    rm -f "${BACKUP_FILE}"
    exit 1
fi

# ------------------- 清理过期备份 -------------------
log_info "清理 ${RETENTION_DAYS} 天前的旧备份..."
find "${BACKUP_DIR}" -name "mysql_backup_*.sql.gz" -type f -mtime "+${RETENTION_DAYS}" -delete
log_info "清理完成"

# ------------------- 日志轮转 -------------------
tail -n 1000 "${LOG_FILE}" > "${LOG_FILE}.tmp" && mv "${LOG_FILE}.tmp" "${LOG_FILE}"

log_info "备份任务完成"
log_info "--------------------------------------------------"