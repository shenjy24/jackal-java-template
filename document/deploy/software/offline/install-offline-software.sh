#!/usr/bin/env bash

set -Eeuo pipefail

# Offline bootstrap script for Git, Docker Engine and Docker Compose.
# Put required packages under ./pkgs by default.

TECH_USER="${TECH_USER:-tech}"
PKG_DIR="${PKG_DIR:-pkgs}"
GIT_ARCHIVE="${GIT_ARCHIVE:-git-binaries.linux-64bit.tar.gz}"
DOCKER_ARCHIVE="${DOCKER_ARCHIVE:-docker-29.5.3.tgz}"
DOCKER_COMPOSE_BINARY="${DOCKER_COMPOSE_BINARY:-docker-compose-linux-x86_64}"

INSTALL_PREFIX="${INSTALL_PREFIX:-/usr/local}"
OPT_DIR="${OPT_DIR:-/opt}"
GIT_INSTALL_DIR="${GIT_INSTALL_DIR:-$OPT_DIR/git-binaries}"
DOCKER_BIN_DIR="${DOCKER_BIN_DIR:-$INSTALL_PREFIX/bin}"
DOCKER_COMPOSE_PLUGIN_DIR="${DOCKER_COMPOSE_PLUGIN_DIR:-$INSTALL_PREFIX/lib/docker/cli-plugins}"

SUDO=""
if [ "$(id -u)" -ne 0 ]; then
  SUDO="sudo"
fi

log() {
  printf '\n[%s] %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$*"
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

require_command() {
  if ! command_exists "$1"; then
    echo "缺少必要命令: $1" >&2
    exit 1
  fi
}

resolve_path() {
  case "$1" in
    /*) printf '%s\n' "$1" ;;
    *) printf '%s\n' "$SCRIPT_DIR/$1" ;;
  esac
}

require_file() {
  local file_path="$1"
  local label="$2"
  if [ ! -f "$file_path" ]; then
    echo "未找到${label}: $file_path" >&2
    exit 1
  fi
}

group_exists() {
  local group_name="$1"
  if command_exists getent; then
    getent group "$group_name" >/dev/null 2>&1
  elif [ -r /etc/group ]; then
    grep -q "^${group_name}:" /etc/group
  else
    return 1
  fi
}

create_tech_user() {
  log "创建用户: $TECH_USER"
  if id "$TECH_USER" >/dev/null 2>&1; then
    echo "用户 $TECH_USER 已存在，跳过创建。"
  else
    $SUDO useradd -m -s /bin/bash "$TECH_USER"
    echo "用户 $TECH_USER 已创建。如需密码登录，请后续执行: sudo passwd $TECH_USER"
  fi

  if command_exists usermod; then
    $SUDO usermod -aG sudo "$TECH_USER" 2>/dev/null || true
    $SUDO usermod -aG wheel "$TECH_USER" 2>/dev/null || true
  fi

  if [ -d /etc/sudoers.d ]; then
    printf '%s ALL=(ALL:ALL) ALL\n' "$TECH_USER" | $SUDO tee "/etc/sudoers.d/$TECH_USER" >/dev/null
    $SUDO chmod 0440 "/etc/sudoers.d/$TECH_USER"
  else
    echo "/etc/sudoers.d 不存在，请按需手动给 $TECH_USER 配置 sudo 权限。"
  fi
}

find_executable() {
  local root_dir="$1"
  local name="$2"
  find "$root_dir" -type f -name "$name" -perm /111 | head -n 1
}

install_git() {
  local archive_path="$1"
  log "安装 Git: $archive_path"

  require_file "$archive_path" "Git 离线包"
  require_command tar

  local tmp_dir
  tmp_dir="$(mktemp -d)"

  tar -xzf "$archive_path" -C "$tmp_dir"

  local git_bin
  git_bin="$(find_executable "$tmp_dir" git)"
  if [ -z "$git_bin" ]; then
    echo "Git 离线包中未找到可执行文件 git: $archive_path" >&2
    exit 1
  fi

  $SUDO rm -rf "$GIT_INSTALL_DIR"
  $SUDO mkdir -p "$GIT_INSTALL_DIR"
  $SUDO cp -a "$tmp_dir"/. "$GIT_INSTALL_DIR"/

  git_bin="${git_bin#$tmp_dir/}"
  git_bin="$GIT_INSTALL_DIR/$git_bin"

  local git_exec_path
  git_exec_path="$(find "$GIT_INSTALL_DIR" -type d -path "*/libexec/git-core" | head -n 1 || true)"
  if [ -z "$git_exec_path" ]; then
    git_exec_path="$(find "$GIT_INSTALL_DIR" -type d -path "*/git-core" | head -n 1 || true)"
  fi

  $SUDO mkdir -p "$INSTALL_PREFIX/bin"
  if [ -n "$git_exec_path" ]; then
    $SUDO sh -c "cat > '$INSTALL_PREFIX/bin/git'" <<EOF
#!/bin/sh
export GIT_EXEC_PATH='$git_exec_path'
exec '$git_bin' "\$@"
EOF
  else
    $SUDO sh -c "cat > '$INSTALL_PREFIX/bin/git'" <<EOF
#!/bin/sh
exec '$git_bin' "\$@"
EOF
  fi
  $SUDO chmod 0755 "$INSTALL_PREFIX/bin/git"

  "$INSTALL_PREFIX/bin/git" --version
  rm -rf "$tmp_dir"
}

install_docker_engine() {
  local archive_path="$1"
  log "安装 Docker Engine: $archive_path"

  require_file "$archive_path" "Docker 离线包"
  require_command tar

  local tmp_dir
  tmp_dir="$(mktemp -d)"

  tar -xzf "$archive_path" -C "$tmp_dir"
  if [ ! -d "$tmp_dir/docker" ]; then
    echo "Docker 离线包结构不符合预期，未找到 docker/ 目录: $archive_path" >&2
    exit 1
  fi

  $SUDO mkdir -p "$DOCKER_BIN_DIR"
  $SUDO cp "$tmp_dir"/docker/* "$DOCKER_BIN_DIR"/
  $SUDO chmod 0755 "$DOCKER_BIN_DIR"/docker "$DOCKER_BIN_DIR"/dockerd "$DOCKER_BIN_DIR"/containerd "$DOCKER_BIN_DIR"/runc 2>/dev/null || true

  if ! group_exists docker; then
    $SUDO groupadd docker
  fi

  if command_exists usermod; then
    $SUDO usermod -aG docker "$TECH_USER" 2>/dev/null || true
  fi

  install_docker_systemd_units
  "$DOCKER_BIN_DIR/docker" --version
  rm -rf "$tmp_dir"
}

install_docker_compose() {
  local compose_path="$1"
  log "安装 Docker Compose: $compose_path"

  require_file "$compose_path" "Docker Compose 离线二进制"

  $SUDO mkdir -p "$DOCKER_COMPOSE_PLUGIN_DIR" "$INSTALL_PREFIX/bin"
  $SUDO cp "$compose_path" "$DOCKER_COMPOSE_PLUGIN_DIR/docker-compose"
  $SUDO chmod 0755 "$DOCKER_COMPOSE_PLUGIN_DIR/docker-compose"
  $SUDO ln -sfn "$DOCKER_COMPOSE_PLUGIN_DIR/docker-compose" "$INSTALL_PREFIX/bin/docker-compose"

  "$DOCKER_BIN_DIR/docker" compose version || "$INSTALL_PREFIX/bin/docker-compose" version
}

install_docker_systemd_units() {
  if ! command_exists systemctl; then
    echo "未找到 systemctl，请手动启动 dockerd。"
    return
  fi

  log "安装 Docker systemd 服务"
  $SUDO sh -c "cat > /etc/systemd/system/containerd.service" <<EOF
[Unit]
Description=containerd container runtime
Documentation=https://containerd.io
After=network.target local-fs.target

[Service]
ExecStart=$DOCKER_BIN_DIR/containerd
Restart=always
RestartSec=5
Delegate=yes
KillMode=process
OOMScoreAdjust=-999
LimitNOFILE=1048576
LimitNPROC=infinity
LimitCORE=infinity

[Install]
WantedBy=multi-user.target
EOF

  $SUDO sh -c "cat > /etc/systemd/system/docker.service" <<EOF
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target containerd.service
Wants=network-online.target
Requires=containerd.service

[Service]
Type=notify
ExecStart=$DOCKER_BIN_DIR/dockerd --containerd=/run/containerd/containerd.sock
ExecReload=/bin/kill -s HUP \$MAINPID
TimeoutStartSec=0
Restart=always
RestartSec=2
StartLimitBurst=3
StartLimitInterval=60s
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
TasksMax=infinity
Delegate=yes
KillMode=process
OOMScoreAdjust=-500

[Install]
WantedBy=multi-user.target
EOF

  $SUDO systemctl daemon-reload
  $SUDO systemctl enable --now containerd
  $SUDO systemctl enable --now docker
}

main() {
  if [ "$(id -u)" -ne 0 ]; then
    require_command sudo
  fi

  SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  PKG_DIR="$(resolve_path "$PKG_DIR")"

  local git_archive_path="$PKG_DIR/$GIT_ARCHIVE"
  local docker_archive_path="$PKG_DIR/$DOCKER_ARCHIVE"
  local compose_binary_path="$PKG_DIR/$DOCKER_COMPOSE_BINARY"

  create_tech_user
  install_git "$git_archive_path"
  install_docker_engine "$docker_archive_path"
  install_docker_compose "$compose_binary_path"

  echo
  echo "离线软件安装完成。"
  echo "如本次修改了 docker 用户组，请重新登录 $TECH_USER 后再直接执行 docker 命令。"
}

main "$@"
