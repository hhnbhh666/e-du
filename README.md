# 网易云课堂 - 教育类 APP

一个仿网易云课堂布局的跨平台教育应用，包含课程学习和驾考刷题两大核心模块。

## 项目结构

```
.
├── wy-uni/                 # UniApp 前端项目
│   ├── pages/
│   │   ├── index/          # 首页/找课页面
│   │   ├── quiz/           # 刷题页面（学生端）
│   │   └── teacher/        # 老师功能页面
│   │       ├── add-question.vue    # 添加题目
│   │       └── doc-import.vue      # OCR文档导入
│   └── static/             # 静态资源
│
└── wy-spring/              # Spring Boot 后端项目（待实现）
```

## 技术栈

### 前端
- **框架**: UniApp + Vue3
- **UI**: 自定义组件 + 原生样式
- **富文本**: 微信小程序 Editor 组件
- **跨平台**: 支持微信小程序、H5、App

### 后端（计划中）
- **框架**: Spring Boot 2.7+
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **缓存**: Redis
- **安全**: JWT + Spring Security
- **文档**: Swagger/OpenAPI


## 快速开始

### 前端运行

```bash
cd wy-uni

# 安装依赖
npm install

# 运行到微信小程序
npm run dev:mp-weixin

# 运行到 H5
npm run dev:h5

# 运行到 App
npm run dev:app
```

### 后端启动（待实现）

```bash
cd wy-spring

# Maven 编译
mvn clean install

# 启动服务
mvn spring-boot:run
```

## 环境要求

- Node.js 16+
- JDK 1.8+
- MySQL 8.0+
- Redis 6.0+
- 微信开发者工具（小程序开发）
- HBuilderX 或 VSCode（推荐）
