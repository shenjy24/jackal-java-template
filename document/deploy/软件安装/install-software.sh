#!/usr/bin/env bash

set -Eeuo pipefail

# Common server bootstrap script for steps 1-7 in software installation docs.
# MySQL is intentionally excluded.

TECH_USER="${TECH_USER:-tech}"
NGINX_USER="${NGINX_USER:-root}"
SKIP_NODE="${SKIP_NODE:-0}"

# Fixed software versions. Update these values intentionally when upgrading.
GIT_VERSION="${GIT_VERSION:-2.43.0}"
JDK_VERSION="${JDK_VERSION:-21.0.8}"
MAVEN_VERSION="${MAVEN_VERSION:-3.9.11}"
DOCKER_VERSION="${DOCKER_VERSION:-28.3.3}"
CONTAINERD_VERSION="${CONTAINERD_VERSION:-1.7.27}"
DOCKER_BUILDX_VERSION="${DOCKER_BUILDX_VERSION:-0.26.1}"
DOCKER_COMPOSE_VERSION="${DOCKER_COMPOSE_VERSION:-2.39.1}"
NODE_VERSION="${NODE_VERSION:-20.19.4}"
PNPM_VERSION="${PNPM_VERSION:-10.15.0}"
NGINX_VERSION="${NGINX_VERSION:-1.24.0}"

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
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

detect_os() {
  if [ -r /etc/os-release ]; then
    # shellcheck disable=SC1091
    . /etc/os-release
    OS_ID="${ID:-}"
    OS_ID_LIKE="${ID_LIKE:-}"
    OS_VERSION_ID="${VERSION_ID:-}"
  else
    OS_ID=""
    OS_ID_LIKE=""
    OS_VERSION_ID=""
  fi

  if command_exists apt-get; then
    PKG_MANAGER="apt"
  elif command_exists dnf; then
    PKG_MANAGER="dnf"
  elif command_exists yum; then
    PKG_MANAGER="yum"
  else
    echo "Unsupported system: apt, dnf or yum is required." >&2
    exit 1
  fi
}

pkg_install() {
  case "$PKG_MANAGER" in
    apt)
      $SUDO apt-get update
      DEBIAN_FRONTEND=noninteractive $SUDO apt-get install -y "$@"
      ;;
    dnf)
      $SUDO dnf install -y "$@"
      ;;
    yum)
      $SUDO yum install -y "$@"
      ;;
  esac
}

apt_install_fixed() {
  $SUDO apt-get update
  DEBIAN_FRONTEND=noninteractive $SUDO apt-get install -y "$@"
}

rpm_install_fixed() {
  case "$PKG_MANAGER" in
    dnf) $SUDO dnf install -y "$@" ;;
    yum) $SUDO yum install -y "$@" ;;
  esac
}

resolve_apt_version() {
  local package_name="$1"
  local version_prefix="$2"
  apt-cache madison "$package_name" | awk -v prefix="$version_prefix" '$3 ~ "(^|:)" prefix { print $3; exit }'
}

resolve_rpm_version() {
  local package_name="$1"
  local version_prefix="$2"
  case "$PKG_MANAGER" in
    dnf)
      $SUDO dnf --showduplicates list "$package_name" 2>/dev/null | awk -v prefix="$version_prefix" '$2 ~ "(^|:)" prefix { version=$2 } END { print version }'
      ;;
    yum)
      $SUDO yum --showduplicates list "$package_name" 2>/dev/null | awk -v prefix="$version_prefix" '$2 ~ "(^|:)" prefix { version=$2 } END { print version }'
      ;;
  esac
}

ensure_command_version() {
  local command_name="$1"
  local expected_version="$2"
  shift 2

  if command_exists "$command_name"; then
    local version_output
    version_output="$("$@" 2>&1 || true)"
    if printf '%s\n' "$version_output" | grep -q "$expected_version"; then
      printf '%s\n' "$version_output"
      return 0
    fi

    echo "$command_name is already installed, but version is not $expected_version:" >&2
    printf '%s\n' "$version_output" >&2
    echo "Please uninstall it first or change the fixed version variable in this script." >&2
    exit 1
  fi

  return 1
}

install_apt_version_prefix() {
  local package_name="$1"
  local version_prefix="$2"
  local resolved_version
  resolved_version="$(resolve_apt_version "$package_name" "$version_prefix")"
  if [ -z "$resolved_version" ]; then
    echo "Package $package_name version $version_prefix is not available in apt repositories." >&2
    exit 1
  fi
  apt_install_fixed "$package_name=$resolved_version"
}

install_rpm_version_prefix() {
  local package_name="$1"
  local version_prefix="$2"
  local resolved_version
  resolved_version="$(resolve_rpm_version "$package_name" "$version_prefix")"
  if [ -z "$resolved_version" ]; then
    echo "Package $package_name version $version_prefix is not available in rpm repositories." >&2
    exit 1
  fi
  rpm_install_fixed "$package_name-$resolved_version"
}

install_package_version_prefix() {
  local package_name="$1"
  local version_prefix="$2"
  case "$PKG_MANAGER" in
    apt) install_apt_version_prefix "$package_name" "$version_prefix" ;;
    dnf|yum) install_rpm_version_prefix "$package_name" "$version_prefix" ;;
  esac
}

enable_service() {
  local service_name="$1"
  if command_exists systemctl; then
    $SUDO systemctl enable --now "$service_name"
  else
    echo "systemctl not found, please start $service_name manually."
  fi
}

install_base_tools() {
  log "Installing base tools"
  case "$PKG_MANAGER" in
    apt)
      pkg_install ca-certificates curl gnupg lsb-release wget tar
      ;;
    dnf|yum)
      pkg_install ca-certificates curl wget tar
      ;;
  esac
}

create_tech_user() {
  log "Creating user: $TECH_USER"
  if id "$TECH_USER" >/dev/null 2>&1; then
    echo "User $TECH_USER already exists, skipped."
  else
    $SUDO useradd -m -s /bin/bash "$TECH_USER"
    echo "User $TECH_USER created. Run 'sudo passwd $TECH_USER' later if password login is needed."
  fi

  if command_exists usermod; then
    $SUDO usermod -aG sudo "$TECH_USER" 2>/dev/null || true
    $SUDO usermod -aG wheel "$TECH_USER" 2>/dev/null || true
  fi

  if [ -d /etc/sudoers.d ]; then
    printf '%s ALL=(ALL:ALL) ALL\n' "$TECH_USER" | $SUDO tee "/etc/sudoers.d/$TECH_USER" >/dev/null
    $SUDO chmod 0440 "/etc/sudoers.d/$TECH_USER"
  else
    echo "/etc/sudoers.d not found, please add sudo permission for $TECH_USER manually."
  fi
}

install_git() {
  log "Installing Git $GIT_VERSION"
  if ensure_command_version git "$GIT_VERSION" git --version; then
    return
  fi
  install_package_version_prefix git "$GIT_VERSION"
  git --version
}

install_jdk21() {
  log "Installing JDK $JDK_VERSION"
  if ensure_command_version java "$JDK_VERSION" java -version; then
    return
  fi

  case "$PKG_MANAGER" in
    apt)
      install_package_version_prefix openjdk-21-jdk "$JDK_VERSION"
      ;;
    dnf|yum)
      install_package_version_prefix java-21-openjdk-devel "$JDK_VERSION"
      ;;
  esac

  java -version
}

install_maven() {
  log "Installing Maven $MAVEN_VERSION"
  if ensure_command_version mvn "$MAVEN_VERSION" mvn --version; then
    return
  fi

  local archive_name="apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  local download_url="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${archive_name}"
  local install_dir="/opt/apache-maven-${MAVEN_VERSION}"

  $SUDO mkdir -p /opt
  curl -fsSL "$download_url" -o "/tmp/$archive_name"
  $SUDO tar -xzf "/tmp/$archive_name" -C /opt
  $SUDO ln -sfn "$install_dir" /opt/maven
  $SUDO ln -sfn /opt/maven/bin/mvn /usr/local/bin/mvn
  mvn --version
}

install_docker_apt() {
  local repo_os="$OS_ID"
  [ "$repo_os" != "ubuntu" ] && [ "$repo_os" != "debian" ] && repo_os="ubuntu"

  if [ ! -f /etc/apt/sources.list.d/docker.list ]; then
    $SUDO install -m 0755 -d /etc/apt/keyrings
    curl -fsSL "https://download.docker.com/linux/$repo_os/gpg" | $SUDO gpg --dearmor --yes -o /etc/apt/keyrings/docker.gpg
    $SUDO chmod a+r /etc/apt/keyrings/docker.gpg

    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/$repo_os $(. /etc/os-release && echo "${VERSION_CODENAME:-}") stable" \
      | $SUDO tee /etc/apt/sources.list.d/docker.list >/dev/null
  fi

  $SUDO apt-get update
  install_apt_version_prefix docker-ce "${DOCKER_VERSION}"
  install_apt_version_prefix docker-ce-cli "${DOCKER_VERSION}"
  install_apt_version_prefix containerd.io "${CONTAINERD_VERSION}"
  install_apt_version_prefix docker-buildx-plugin "${DOCKER_BUILDX_VERSION}"
  install_apt_version_prefix docker-compose-plugin "${DOCKER_COMPOSE_VERSION}"
}

install_docker_yum_or_dnf() {
  if [ "$PKG_MANAGER" = "dnf" ]; then
    pkg_install dnf-plugins-core || pkg_install yum-utils
    $SUDO dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    install_rpm_version_prefix docker-ce "${DOCKER_VERSION}"
    install_rpm_version_prefix docker-ce-cli "${DOCKER_VERSION}"
    install_rpm_version_prefix containerd.io "${CONTAINERD_VERSION}"
    install_rpm_version_prefix docker-buildx-plugin "${DOCKER_BUILDX_VERSION}"
    install_rpm_version_prefix docker-compose-plugin "${DOCKER_COMPOSE_VERSION}"
  else
    pkg_install yum-utils device-mapper-persistent-data lvm2
    $SUDO yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    install_rpm_version_prefix docker-ce "${DOCKER_VERSION}"
    install_rpm_version_prefix docker-ce-cli "${DOCKER_VERSION}"
    install_rpm_version_prefix containerd.io "${CONTAINERD_VERSION}"
    install_rpm_version_prefix docker-buildx-plugin "${DOCKER_BUILDX_VERSION}"
    install_rpm_version_prefix docker-compose-plugin "${DOCKER_COMPOSE_VERSION}"
  fi
}

install_docker() {
  log "Installing Docker $DOCKER_VERSION"
  if ensure_command_version docker "$DOCKER_VERSION" docker --version; then
    enable_service docker
    return
  fi
  case "$PKG_MANAGER" in
    apt) install_docker_apt ;;
    dnf|yum) install_docker_yum_or_dnf ;;
  esac

  enable_service docker
  docker --version || $SUDO docker --version

  if command_exists usermod; then
    $SUDO usermod -aG docker "$TECH_USER" 2>/dev/null || true
  fi
}

install_node() {
  if [ "$SKIP_NODE" = "1" ]; then
    log "Skipping Node.js"
    return
  fi

  log "Installing Node.js $NODE_VERSION"
  if ensure_command_version node "v$NODE_VERSION" node -v; then
    ensure_command_version pnpm "$PNPM_VERSION" pnpm -v || $SUDO npm install -g "pnpm@$PNPM_VERSION"
    return
  fi

  local arch
  case "$(uname -m)" in
    x86_64|amd64) arch="x64" ;;
    aarch64|arm64) arch="arm64" ;;
    *)
      echo "Unsupported Node.js architecture: $(uname -m)" >&2
      exit 1
      ;;
  esac

  local node_dir="node-v${NODE_VERSION}-linux-${arch}"
  local archive_name="${node_dir}.tar.xz"
  $SUDO mkdir -p /opt
  curl -fsSL "https://nodejs.org/dist/v${NODE_VERSION}/${archive_name}" -o "/tmp/$archive_name"
  $SUDO tar -xJf "/tmp/$archive_name" -C /opt
  $SUDO ln -sfn "/opt/$node_dir" /opt/node
  $SUDO ln -sfn /opt/node/bin/node /usr/local/bin/node
  $SUDO ln -sfn /opt/node/bin/npm /usr/local/bin/npm
  $SUDO ln -sfn /opt/node/bin/npx /usr/local/bin/npx

  node -v
  npm -v
  $SUDO npm install -g "pnpm@$PNPM_VERSION"
  $SUDO ln -sfn /opt/node/bin/pnpm /usr/local/bin/pnpm
  pnpm -v
}

install_nginx() {
  log "Installing Nginx $NGINX_VERSION"
  if ensure_command_version nginx "$NGINX_VERSION" nginx -v; then
    enable_service nginx
    return
  fi
  if [ "$PKG_MANAGER" = "yum" ]; then
    pkg_install epel-release || true
  fi
  install_package_version_prefix nginx "$NGINX_VERSION"

  if [ -f /etc/nginx/nginx.conf ] && [ -n "$NGINX_USER" ]; then
    $SUDO sed -i.bak -E "s/^user[[:space:]]+[^;]+;/user $NGINX_USER;/" /etc/nginx/nginx.conf || true
  fi

  enable_service nginx
  nginx -v
}

main() {
  if [ "$(id -u)" -ne 0 ]; then
    require_command sudo
  fi
  detect_os
  log "Detected package manager: $PKG_MANAGER, os: ${OS_ID:-unknown} ${OS_VERSION_ID:-}"

  install_base_tools
  create_tech_user
  install_git
  install_jdk21
  install_maven
  install_docker
  install_node
  install_nginx

  echo "If docker group membership was changed, log out and log in again for it to take effect."
}

main "$@"
