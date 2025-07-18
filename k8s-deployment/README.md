# SE2303 微服务云部署方案

## 概述

本项目提供了一套完整的 SE2303 心理健康微服务系统的云部署方案，支持 Kubernetes 集群部署、分组管理、动态扩缩容等功能。

## 系统架构

### 服务分组

- **基础设施组 (Infrastructure)**: MySQL、Redis、Elasticsearch
- **网关组 (Gateway)**: se2303-gateway
- **核心业务组 (Core Business)**: user-service、community-service
- **功能增强组 (Feature Enhancement)**: mood-service、alert-service
- **实时通信组 (Realtime Communication)**: bot-service、talk-service
- **监管服务组 (Monitoring & Management)**: notification-service、inspection-service

### 技术栈

- **容器化**: Docker
- **编排**: Kubernetes
- **服务发现**: Kubernetes DNS
- **负载均衡**: Kubernetes Service + Ingress
- **自动扩缩容**: HorizontalPodAutoscaler (HPA)
- **数据持久化**: PersistentVolume
- **配置管理**: ConfigMap + Secret

## 前置条件

### 基础环境

1. **Kubernetes 集群** (版本 >= 1.20)
   - 可以是云服务商提供的托管集群（如阿里云 ACK、腾讯云 TKE）
   - 也可以是自建集群

2. **kubectl 工具**
   ```bash
   # 安装 kubectl
   curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
   sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
   ```

3. **Docker** (用于构建镜像)
   ```bash
   # 安装 Docker
   curl -fsSL https://get.docker.com -o get-docker.sh
   sh get-docker.sh
   ```

4. **Metrics Server** (用于 HPA)
   ```bash
   kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
   ```

### 资源需求

推荐最小配置：
- **CPU**: 8 核
- **内存**: 16GB
- **存储**: 100GB SSD
- **节点数**: 3 个

## 快速开始

### 1. 准备工作

```bash
# 克隆项目
git clone <your-repo-url>
cd se2303-backend/k8s-deployment

# 设置执行权限
chmod +x *.sh

# 设置域名环境变量（替换为您的实际域名）
export DOMAIN=your-domain.com
```

### 2. 构建 Docker 镜像

```bash
# 构建所有微服务镜像
./build-images.sh

# 或者构建指定版本
./build-images.sh v1.0.0

# 推送镜像到容器仓库（如果使用远程仓库）
docker push se2303/gateway:latest
docker push se2303/user-service:latest
# ... 其他镜像
```

### 3. 部署系统

```bash
# 完整部署
./deploy.sh deploy

# 或者分组部署
./deploy.sh deploy-group infrastructure  # 先部署基础设施
./deploy.sh deploy-group gateway         # 再部署网关
./deploy.sh deploy-group core-business   # 部署核心业务服务
# ... 继续部署其他组
```

### 4. 验证部署

```bash
# 查看部署状态
./deploy.sh status

# 查看所有 Pod 状态
kubectl get pods --all-namespaces -l app.kubernetes.io/part-of=se2303-backend

# 查看服务状态
kubectl get svc --all-namespaces -l app.kubernetes.io/part-of=se2303-backend
```

## 详细配置

### 域名配置

在部署前，您需要配置实际的域名：

1. **设置环境变量**：
   ```bash
   export DOMAIN=api.yourdomain.com
   ```

2. **DNS 配置**：
   - 将域名解析到 Kubernetes Ingress Controller 的外部 IP
   - 如果使用云服务商，通常会自动分配负载均衡器

3. **SSL 证书**：
   ```bash
   # 创建 TLS Secret（如果有证书）
   kubectl create secret tls se2303-tls-secret \
     --cert=path/to/cert.pem \
     --key=path/to/key.pem \
     -n se2303-gateway
   ```

### 数据库配置

默认配置使用容器化的 MySQL，密码为 `123456`。生产环境建议：

1. **使用云数据库**：
   - 修改 `04-configmaps-secrets.yaml` 中的数据库连接信息
   - 使用云服务商提供的 RDS/CDB 等托管数据库

2. **数据持久化**：
   - 配置合适的 StorageClass
   - 调整 PVC 大小以满足业务需求

### 资源配置

根据业务需求调整各服务的资源配置：

```yaml
# 在相应的 deployment 文件中调整
resources:
  limits:
    cpu: 2000m
    memory: 2Gi
  requests:
    cpu: 1000m
    memory: 1Gi
```

## 运维操作

### 扩缩容

```bash
# 手动扩缩容
./deploy.sh scale user-service 5 se2303-core-business

# 查看 HPA 状态
kubectl get hpa --all-namespaces

# 调整 HPA 参数
kubectl edit hpa user-service-hpa -n se2303-core-business
```

### 监控日志

```bash
# 查看服务日志
kubectl logs -f deployment/user-service -n se2303-core-business

# 查看多个副本的日志
kubectl logs -f -l app=user-service -n se2303-core-business

# 进入容器调试
kubectl exec -it <pod-name> -n <namespace> -- /bin/bash
```

### 更新部署

```bash
# 更新镜像
kubectl set image deployment/user-service user-service=se2303/user-service:v1.1.0 -n se2303-core-business

# 查看更新状态
kubectl rollout status deployment/user-service -n se2303-core-business

# 回滚更新
kubectl rollout undo deployment/user-service -n se2303-core-business
```

### 备份恢复

```bash
# 备份数据库
kubectl exec mysql-xxx -n se2303-infrastructure -- mysqldump -u root -p123456 --all-databases > backup.sql

# 恢复数据库
kubectl exec -i mysql-xxx -n se2303-infrastructure -- mysql -u root -p123456 < backup.sql
```

## 故障排查

### 常见问题

1. **Pod 无法启动**
   ```bash
   kubectl describe pod <pod-name> -n <namespace>
   kubectl logs <pod-name> -n <namespace>
   ```

2. **服务无法访问**
   ```bash
   kubectl get svc -n <namespace>
   kubectl get endpoints -n <namespace>
   ```

3. **HPA 不工作**
   ```bash
   kubectl describe hpa <hpa-name> -n <namespace>
   kubectl top pods -n <namespace>
   ```

4. **存储问题**
   ```bash
   kubectl get pv
   kubectl get pvc -n <namespace>
   ```

### 调试命令

```bash
# 查看集群信息
kubectl cluster-info

# 查看节点状态
kubectl get nodes -o wide

# 查看资源使用情况
kubectl top nodes
kubectl top pods --all-namespaces

# 查看事件
kubectl get events --all-namespaces --sort-by='.metadata.creationTimestamp'
```

## 安全配置

### 网络策略

```yaml
# 示例：限制跨命名空间访问
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-all
  namespace: se2303-core-business
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
```

### RBAC 配置

```yaml
# 为部署创建专用的 ServiceAccount
apiVersion: v1
kind: ServiceAccount
metadata:
  name: se2303-deployer
  namespace: default
```

### 密钥管理

- 使用 Kubernetes Secrets 存储敏感信息
- 定期轮换密钥
- 考虑使用外部密钥管理系统（如 Vault）

## 性能优化

### 资源优化

1. **合理设置资源限制**
2. **使用节点亲和性**
3. **配置 Pod 反亲和性避免单点故障**

### 镜像优化

1. **使用多阶段构建减小镜像大小**
2. **使用镜像缓存加速构建**
3. **定期清理无用镜像**

## 清理环境

```bash
# 清理所有部署资源
./deploy.sh cleanup

# 清理 Docker 镜像
docker system prune -a
```

## 支持与贡献

如有问题，请提交 Issue 或联系项目维护者。

## 许可证

本项目采用 MIT 许可证。 