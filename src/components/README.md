# 组件结构说明

## 目录结构
```
src/
├── components/          # 通用组件文件夹（仅Ant Design相关）
│   ├── index.js        # 组件统一导出文件
│   └── README.md       # 组件说明文档
├── pages/              # 页面组件文件夹
│   ├── AIChat.jsx      # AI聊天页面
│   ├── Community.jsx   # 社区页面
│   ├── Diary.jsx       # 日记页面
│   └── ...             # 其他页面
└── css/                # 样式文件文件夹
    ├── AIChat.css      # AI聊天页面样式
    ├── Community.css   # 社区页面样式
    ├── Diary.css       # 日记页面样式
    └── ...             # 其他页面样式
```

## 组件分类

### 通用组件 (src/components/)
- **用途**: 仅存放基于Ant Design的通用组件
- **特点**: 可复用、无具体业务逻辑
- **示例**: 通用按钮、通用卡片、通用表单等
- **不包含**: 具体页面组件（如AIChat）

### 页面组件 (src/pages/)
- **用途**: 存放所有具体页面组件
- **特点**: 包含完整的页面逻辑和业务功能
- **示例**: AIChat、Community、Diary等
- **样式**: 对应的CSS文件在 `src/css/` 目录

## 样式文件 (src/css/)
- **位置**: 所有CSS文件统一放在 `src/css/` 目录下
- **命名**: 与页面组件名称对应
- **设计**: 蓝白极简风格，主色调 #1890ff
- **特性**: 支持响应式设计和深色模式

## 开发规范

### 通用组件开发
1. 在 `src/components/` 创建组件文件
2. 基于Ant Design组件进行封装
3. 保持组件的通用性和可复用性
4. 在 `src/components/index.js` 中导出

### 页面组件开发
1. 在 `src/pages/` 创建页面组件文件
2. 在 `src/css/` 创建对应的样式文件
3. 在页面组件中导入样式文件
4. 使用Ant Design组件构建页面

## 使用示例

### 页面组件使用
```jsx
// 在路由或其他地方使用页面组件
import AIChat from '../pages/AIChat';
```

### 通用组件使用（如果有）
```jsx
// 在页面中使用通用组件
import { CommonButton, CommonCard } from '../components';
```

## 注意事项
- 不要将具体页面组件放在 `src/components/` 目录下
- 所有样式文件必须放在 `src/css/` 目录下
- 保持组件和样式的命名一致性
- 遵循蓝白极简的设计风格 