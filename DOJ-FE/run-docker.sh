#!/bin/bash

# 脚本执行时，如果任何命令失败，则立即退出
set -e

# 定义镜像和容器的名称
IMAGE_NAME="doj-fe"
CONTAINER_NAME="doj-fe-container"

# 1. 构建最新的前端镜像
echo "Building Docker image: $IMAGE_NAME..."
docker build -t $IMAGE_NAME .

# 2. 检查是否存在同名容器，如果存在则停止并删除
if [ $(docker ps -a -q -f name=^/${CONTAINER_NAME}$) ]; then
    echo "Stopping and removing existing container: $CONTAINER_NAME..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
fi

# 3. 运行新的容器
echo "Running new container: $CONTAINER_NAME on http://localhost:8088"
docker run -d --name $CONTAINER_NAME -p 8088:80 $IMAGE_NAME

echo "Deployment successful!"
