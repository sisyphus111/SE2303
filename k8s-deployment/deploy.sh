#!/bin/bash

# SE2303 微服务一键部署脚本
# 作者: SE2303项目组
# 说明: 支持分组部署、扩缩容、监控等功能

set -e

# 配置变量
NAMESPACE_PREFIX="se2303"
DOMAIN="${DOMAIN:-your-domain.com}"  # 从环境变量获取域名，默认值为your-domain.com

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
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

log_step() {
    echo -e "${PURPLE}[STEP]${NC} $1"
}

# 检查前置条件
check_prerequisites() {
    log_step "检查前置条件..."
    
    # 检查kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl未安装，请先安装kubectl"
        exit 1
    fi
    
    # 检查集群连接
    if ! kubectl cluster-info &> /dev/null; then
        log_error "无法连接到Kubernetes集群，请检查kubeconfig配置"
        exit 1
    fi
    
    # 检查metrics-server（HPA需要）
    if ! kubectl get deployment metrics-server -n kube-system &> /dev/null; then
        log_warning "metrics-server未安装，HPA功能可能无法正常工作"
        log_info "可以使用以下命令安装metrics-server:"
        log_info "kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml"
    fi
    
    log_success "前置条件检查完成"
}

# 更新配置文件中的域名
update_domain_config() {
    local domain=$1
    log_step "更新配置文件中的域名为: ${domain}"
    
    # 更新Ingress配置中的域名
    sed -i "s/your-domain\\.com/${domain}/g" 05-gateway.yaml
    
    # 更新user-service配置中的cookie域名
    sed -i "s/your-domain\\.com/${domain}/g" 06-core-business-services.yaml
    
    log_success "域名配置更新完成"
}

# 部署命名空间
deploy_namespaces() {
    log_step "部署命名空间..."
    kubectl apply -f 00-namespaces.yaml
    log_success "命名空间部署完成"
}

# 部署基础设施服务
deploy_infrastructure() {
    log_step "部署基础设施服务（MySQL、Redis、Elasticsearch）..."
    
    kubectl apply -f 01-infrastructure-mysql.yaml
    kubectl apply -f 02-infrastructure-redis.yaml
    kubectl apply -f 03-infrastructure-elasticsearch.yaml
    
    log_info "等待基础设施服务启动..."
    
    # 等待MySQL启动
    kubectl wait --for=condition=ready pod -l app=mysql -n ${NAMESPACE_PREFIX}-infrastructure --timeout=300s
    log_success "MySQL启动完成"
    
    # 等待Redis启动
    kubectl wait --for=condition=ready pod -l app=redis -n ${NAMESPACE_PREFIX}-infrastructure --timeout=300s
    log_success "Redis启动完成"
    
    # 等待Elasticsearch启动
    kubectl wait --for=condition=ready pod -l app=elasticsearch -n ${NAMESPACE_PREFIX}-infrastructure --timeout=600s
    log_success "Elasticsearch启动完成"
    
    log_success "基础设施服务部署完成"
}

# 部署配置
deploy_configs() {
    log_step "部署配置（ConfigMaps和Secrets）..."
    kubectl apply -f 04-configmaps-secrets.yaml
    log_success "配置部署完成"
}

# 部署网关
deploy_gateway() {
    log_step "部署网关服务..."
    kubectl apply -f 05-gateway.yaml
    
    log_info "等待网关服务启动..."
    kubectl wait --for=condition=ready pod -l app=se2303-gateway -n ${NAMESPACE_PREFIX}-gateway --timeout=300s
    
    log_success "网关服务部署完成"
}

# 部署核心业务服务
deploy_core_business() {
    log_step "部署核心业务服务（User、Community）..."
    kubectl apply -f 06-core-business-services.yaml
    
    log_info "等待核心业务服务启动..."
    kubectl wait --for=condition=ready pod -l app=user-service -n ${NAMESPACE_PREFIX}-core-business --timeout=300s
    kubectl wait --for=condition=ready pod -l app=community-service -n ${NAMESPACE_PREFIX}-core-business --timeout=300s
    
    log_success "核心业务服务部署完成"
}

# 部署功能增强服务
deploy_feature_enhancement() {
    log_step "部署功能增强服务（Mood、Alert）..."
    kubectl apply -f 07-feature-enhancement-services.yaml
    
    log_info "等待功能增强服务启动..."
    kubectl wait --for=condition=ready pod -l app=mood-service -n ${NAMESPACE_PREFIX}-feature-enhancement --timeout=300s
    kubectl wait --for=condition=ready pod -l app=alert-service -n ${NAMESPACE_PREFIX}-feature-enhancement --timeout=300s
    
    log_success "功能增强服务部署完成"
}

# 部署实时通信服务
deploy_realtime_communication() {
    log_step "部署实时通信服务（Bot、Talk）..."
    kubectl apply -f 08-realtime-communication-services.yaml
    
    log_info "等待实时通信服务启动..."
    kubectl wait --for=condition=ready pod -l app=bot-service -n ${NAMESPACE_PREFIX}-realtime-communication --timeout=300s
    kubectl wait --for=condition=ready pod -l app=talk-service -n ${NAMESPACE_PREFIX}-realtime-communication --timeout=300s
    
    log_success "实时通信服务部署完成"
}

# 部署监管服务
deploy_monitoring() {
    log_step "部署监管服务（Notification、Inspection）..."
    kubectl apply -f 09-monitoring-services.yaml
    
    log_info "等待监管服务启动..."
    kubectl wait --for=condition=ready pod -l app=notification-service -n ${NAMESPACE_PREFIX}-monitoring --timeout=300s
    kubectl wait --for=condition=ready pod -l app=inspection-service -n ${NAMESPACE_PREFIX}-monitoring --timeout=300s
    
    log_success "监管服务部署完成"
}

# 部署HPA
deploy_hpa() {
    log_step "部署HPA自动扩缩容..."
    kubectl apply -f 10-hpa-autoscaling.yaml
    log_success "HPA部署完成"
}

# 完整部署
deploy_all() {
    log_info "开始完整部署SE2303微服务架构..."
    
    check_prerequisites
    
    if [ "${DOMAIN}" != "your-domain.com" ]; then
        update_domain_config "${DOMAIN}"
    else
        log_warning "使用默认域名your-domain.com，建议设置DOMAIN环境变量"
    fi
    
    deploy_namespaces
    deploy_configs
    deploy_infrastructure
    deploy_gateway
    deploy_core_business
    deploy_feature_enhancement
    deploy_realtime_communication
    deploy_monitoring
    deploy_hpa
    
    log_success "🎉 SE2303微服务架构部署完成！"
    
    show_deployment_info
}

# 分组部署
deploy_group() {
    local group=$1
    
    check_prerequisites
    
    case $group in
        infrastructure)
            deploy_namespaces
            deploy_configs
            deploy_infrastructure
            ;;
        gateway)
            deploy_gateway
            ;;
        core-business)
            deploy_core_business
            ;;
        feature-enhancement)
            deploy_feature_enhancement
            ;;
        realtime-communication)
            deploy_realtime_communication
            ;;
        monitoring)
            deploy_monitoring
            ;;
        hpa)
            deploy_hpa
            ;;
        *)
            log_error "未知的服务组: $group"
            log_info "可用的服务组: infrastructure, gateway, core-business, feature-enhancement, realtime-communication, monitoring, hpa"
            exit 1
            ;;
    esac
    
    log_success "服务组 $group 部署完成"
}

# 清理部署
cleanup() {
    log_warning "开始清理SE2303微服务部署..."
    
    # 删除HPA
    kubectl delete -f 10-hpa-autoscaling.yaml --ignore-not-found=true
    
    # 删除微服务
    kubectl delete -f 09-monitoring-services.yaml --ignore-not-found=true
    kubectl delete -f 08-realtime-communication-services.yaml --ignore-not-found=true
    kubectl delete -f 07-feature-enhancement-services.yaml --ignore-not-found=true
    kubectl delete -f 06-core-business-services.yaml --ignore-not-found=true
    kubectl delete -f 05-gateway.yaml --ignore-not-found=true
    
    # 删除配置
    kubectl delete -f 04-configmaps-secrets.yaml --ignore-not-found=true
    
    # 删除基础设施
    kubectl delete -f 03-infrastructure-elasticsearch.yaml --ignore-not-found=true
    kubectl delete -f 02-infrastructure-redis.yaml --ignore-not-found=true
    kubectl delete -f 01-infrastructure-mysql.yaml --ignore-not-found=true
    
    # 删除命名空间（会删除其中的所有资源）
    kubectl delete -f 00-namespaces.yaml --ignore-not-found=true
    
    log_success "清理完成"
}

# 显示部署信息
show_deployment_info() {
    echo ""
    log_info "=== 部署信息 ==="
    
    # 获取Ingress信息
    local ingress_ip=$(kubectl get ingress se2303-gateway-ingress -n ${NAMESPACE_PREFIX}-gateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "未分配")
    local ingress_hostname=$(kubectl get ingress se2303-gateway-ingress -n ${NAMESPACE_PREFIX}-gateway -o jsonpath='{.spec.rules[0].host}' 2>/dev/null || echo "未配置")
    
    echo "访问地址:"
    echo "  域名: https://${ingress_hostname}"
    echo "  IP: ${ingress_ip}"
    echo ""
    
    echo "服务状态:"
    kubectl get pods --all-namespaces -l app.kubernetes.io/part-of=se2303-backend -o wide
    
    echo ""
    echo "HPA状态:"
    kubectl get hpa --all-namespaces
    
    echo ""
    log_info "监控命令:"
    echo "  查看所有服务: kubectl get all --all-namespaces -l app.kubernetes.io/part-of=se2303-backend"
    echo "  查看日志: kubectl logs -f deployment/<service-name> -n <namespace>"
    echo "  进入容器: kubectl exec -it <pod-name> -n <namespace> -- /bin/bash"
}

# 扩缩容操作
scale_service() {
    local service=$1
    local replicas=$2
    local namespace=$3
    
    if [ -z "$service" ] || [ -z "$replicas" ] || [ -z "$namespace" ]; then
        log_error "用法: scale <service> <replicas> <namespace>"
        exit 1
    fi
    
    log_info "扩缩容 $service 到 $replicas 个副本..."
    kubectl scale deployment $service --replicas=$replicas -n $namespace
    
    log_info "等待扩缩容完成..."
    kubectl wait --for=condition=ready pod -l app=$service -n $namespace --timeout=300s
    
    log_success "$service 扩缩容完成"
}

# 帮助信息
show_help() {
    echo "SE2303微服务一键部署脚本"
    echo ""
    echo "用法: $0 <command> [options]"
    echo ""
    echo "命令:"
    echo "  deploy                完整部署所有服务"
    echo "  deploy-group <group>  部署指定服务组"
    echo "  cleanup               清理所有部署资源"
    echo "  status                显示部署状态"
    echo "  scale <svc> <n> <ns>  扩缩容指定服务"
    echo ""
    echo "服务组:"
    echo "  infrastructure        基础设施服务（MySQL、Redis、Elasticsearch）"
    echo "  gateway              网关服务"
    echo "  core-business        核心业务服务（User、Community）"
    echo "  feature-enhancement  功能增强服务（Mood、Alert）"
    echo "  realtime-communication  实时通信服务（Bot、Talk）"
    echo "  monitoring           监管服务（Notification、Inspection）"
    echo "  hpa                  自动扩缩容配置"
    echo ""
    echo "环境变量:"
    echo "  DOMAIN               网关域名（默认: your-domain.com）"
    echo ""
    echo "示例:"
    echo "  $0 deploy                                    # 完整部署"
    echo "  $0 deploy-group infrastructure               # 仅部署基础设施"
    echo "  $0 scale user-service 3 se2303-core-business   # 扩容用户服务到3个副本"
    echo "  DOMAIN=api.example.com $0 deploy             # 使用自定义域名部署"
}

# 主函数
main() {
    local command=$1
    
    case $command in
        deploy)
            deploy_all
            ;;
        deploy-group)
            deploy_group $2
            ;;
        cleanup)
            read -p "确定要清理所有部署资源吗？(y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                cleanup
            else
                log_info "取消清理操作"
            fi
            ;;
        status)
            show_deployment_info
            ;;
        scale)
            scale_service $2 $3 $4
            ;;
        -h|--help|help)
            show_help
            ;;
        *)
            log_error "未知命令: $command"
            show_help
            exit 1
            ;;
    esac
}

# 处理命令行参数
if [ $# -eq 0 ]; then
    show_help
    exit 1
fi

main "$@" 