#!/bin/bash

# SE2303 微服务Docker镜像构建脚本
# 作者: SE2303项目组
# 说明: 自动构建所有微服务的Docker镜像

set -e

# 配置变量
REGISTRY="se2303"  # 可以替换为您的Docker Registry地址
TAG="${1:-latest}"  # 默认标签为latest，可通过参数指定
BUILD_BASE_DIR="../"  # 项目根目录

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 构建单个服务镜像的函数
build_service() {
    local service_name=$1
    local service_path=$2
    local port=$3
    
    log_info "开始构建 ${service_name} 镜像..."
    
    # 检查服务目录是否存在
    if [ ! -d "${BUILD_BASE_DIR}/${service_path}" ]; then
        log_error "服务目录不存在: ${BUILD_BASE_DIR}/${service_path}"
        return 1
    fi
    
    # 创建临时Dockerfile
    local dockerfile_path="${BUILD_BASE_DIR}/${service_path}/Dockerfile"
    cp "dockerfiles/Dockerfile.template" "${dockerfile_path}"
    
    # 替换端口号
    sed -i "s/EXPOSE 8080/EXPOSE ${port}/" "${dockerfile_path}"
    sed -i "s/:8080\/actuator/:${port}\/actuator/" "${dockerfile_path}"
    
    # 构建镜像
    cd "${BUILD_BASE_DIR}/${service_path}"
    
    if docker build -t "${REGISTRY}/${service_name}:${TAG}" .; then
        log_success "${service_name} 镜像构建成功"
    else
        log_error "${service_name} 镜像构建失败"
        return 1
    fi
    
    # 清理临时Dockerfile
    rm -f "${dockerfile_path}"
    
    cd - > /dev/null
}

# 主函数
main() {
    log_info "开始构建SE2303微服务Docker镜像..."
    log_info "Registry: ${REGISTRY}"
    log_info "Tag: ${TAG}"
    
    # 检查Docker是否可用
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装或不在PATH中"
        exit 1
    fi
    
    # 服务列表：服务名称、路径、端口
    declare -a services=(
        "gateway se2303-gateway 8080"
        "user-service user-service 8082"
        "community-service community-service 8083"
        "mood-service mood-service 8085"
        "alert-service alert-service 8086"
        "notification-service notification-service 8087"
        "inspection-service inspection-service 8088"
        "talk-service talk-service 8090"
        "bot-service bot-service 8081"
    )
    
    # 构建计数器
    local success_count=0
    local total_count=${#services[@]}
    
    # 逐个构建服务
    for service_info in "${services[@]}"; do
        read -r service_name service_path port <<< "${service_info}"
        
        if build_service "${service_name}" "${service_path}" "${port}"; then
            ((success_count++))
        fi
        
        echo ""  # 添加空行分隔
    done
    
    # 构建结果统计
    log_info "构建完成统计:"
    log_info "成功: ${success_count}/${total_count}"
    
    if [ ${success_count} -eq ${total_count} ]; then
        log_success "所有镜像构建成功!"
        
        # 显示构建的镜像
        log_info "构建的镜像列表:"
        docker images | grep "${REGISTRY}" | grep "${TAG}"
        
        # 推送提示
        echo ""
        log_info "推送镜像到Registry的命令:"
        for service_info in "${services[@]}"; do
            read -r service_name service_path port <<< "${service_info}"
            echo "  docker push ${REGISTRY}/${service_name}:${TAG}"
        done
        
    else
        log_error "部分镜像构建失败，请检查错误信息"
        exit 1
    fi
}

# 帮助信息
show_help() {
    echo "SE2303微服务Docker镜像构建脚本"
    echo ""
    echo "用法: $0 [TAG]"
    echo ""
    echo "参数:"
    echo "  TAG        镜像标签 (默认: latest)"
    echo ""
    echo "示例:"
    echo "  $0                    # 构建latest标签"
    echo "  $0 v1.0.0             # 构建v1.0.0标签"
    echo "  $0 dev                # 构建dev标签"
    echo ""
    echo "注意:"
    echo "  - 请确保Docker已安装并正在运行"
    echo "  - 请在k8s-deployment目录下执行此脚本"
    echo "  - 构建前请确保各服务的代码已更新"
}

# 处理命令行参数
case "${1}" in
    -h|--help)
        show_help
        exit 0
        ;;
    *)
        main
        ;;
esac 