# Docker 离线镜像目录

执行 `../load-images.sh` 前，请先将 Docker 镜像 tar 文件放到当前目录。

默认文件名如下：

- `mysql-8.4.tar`
- `eclipse-temurin-21-jre.tar`
- `nginx-1.30.2.tar`

其中 Nginx 镜像默认为可选项。是否加载 Java 运行时镜像、Nginx 镜像，以及镜像文件名和镜像 tag，可在上级目录的 `../images.env` 中调整。

通过网盘分享的文件：离线安装包
链接: https://pan.baidu.com/s/1z0nd7Qa3IlgfLe5HsE1o1g 提取码: m7yj