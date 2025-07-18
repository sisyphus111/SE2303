#!/bin/bash

# 启动带有IK分词器的Elasticsearch容器
# 作者: SE2303项目组
# 说明: 一键启动Elasticsearch，端口映射9200->9201，固定密码REDACTED

set -e

CONTAINER_NAME="se2303-elasticsearch"
IMAGE_NAME="davyinsa/elasticsearch-ik:7.17.6"
HOST_PORT="9201"
CONTAINER_PORT="9200"
PASSWORD="REDACTED"

echo "🚀 启动Elasticsearch容器..."

# 检查容器是否已存在
if docker ps -a --format 'table {{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "📦 发现已存在的容器，正在删除..."
    docker rm -f ${CONTAINER_NAME} >/dev/null 2>&1
fi

# 设置vm.max_map_count（仅Linux系统需要）
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "🔧 配置系统参数..."
    sudo sysctl -w vm.max_map_count=262144 >/dev/null 2>&1 || echo "⚠️  无法设置vm.max_map_count，请手动执行: sudo sysctl -w vm.max_map_count=262144"
fi

# 启动容器
docker run -d \
  --name ${CONTAINER_NAME} \
  --restart=unless-stopped \
  -p ${HOST_PORT}:${CONTAINER_PORT} \
  -e "discovery.type=single-node" \
  -e "ES_JAVA_OPTS=-Xms512m -Xmx1g" \
  -e "xpack.security.enabled=true" \
  -e "ELASTIC_PASSWORD=${PASSWORD}" \
  -e "xpack.security.http.ssl.enabled=false" \
  -e "xpack.license.self_generated.type=trial" \
  --log-driver=none \
  ${IMAGE_NAME} >/dev/null 2>&1

# 等待服务启动
echo "⏳ 等待Elasticsearch启动..."
for i in {1..30}; do
    if curl -s -u elastic:${PASSWORD} http://localhost:${HOST_PORT}/_cluster/health >/dev/null 2>&1; then
        echo "✅ Elasticsearch已成功启动!"
        echo "🔗 访问地址: http://localhost:${HOST_PORT}"
        echo "👤 用户名: elastic"
        echo "🔑 密码: ${PASSWORD}"
        echo ""
        echo "🧪 测试IK分词器:"
        echo "curl -u elastic:${PASSWORD} -X POST 'http://localhost:${HOST_PORT}/_analyze' -H 'Content-Type: application/json' -d '{\"analyzer\": \"ik_smart\", \"text\": \"中文分词测试\"}'"
        exit 0
    fi
    sleep 2
done

echo "❌ Elasticsearch启动超时，请检查日志: docker logs ${CONTAINER_NAME}"
exit 1 