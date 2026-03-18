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

## 功能模块

### 1. 找课模块
- 仿网易云课堂首页布局
- 顶部固定搜索栏 + 分类标签
- 分类图标（全部分区 + 可滑动分类）
- 精品推荐、经济、职业等多模块展示
- 吸顶二级导航（滚动时广告隐藏，分类置顶）
- 三级别视频目录结构（竖向滑动模块 + 横向滑动子模块）

### 2. 刷题模块
- 单选题/多选题/判断题
- 答题模式、背题模式、视频模式
- 答题后显示文字解析和视频讲解
- 评论区互动（仅答题后可见）
- 收藏、错题本、答题统计

### 3. 老师模块
- 添加题目（富文本编辑器）
- OCR 识别试卷自动导入（集成百度 OCR）
- 批量导入、创建试卷
- 试卷导出 Word 文档

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

## 开发规范

### Git 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

### 代码规范
- 前端：Vue3 组合式 API，`<script setup>` 语法
- 后端：RESTful API，统一响应格式
- 数据库：软删除、乐观锁、索引优化

## 部署说明

### 小程序部署
1. 使用 HBuilderX 发行 → 微信小程序
2. 上传代码到微信公众平台
3. 配置服务器域名和业务域名
4. 提交审核

### 后端部署
1. 配置 `application-prod.yml`
2. 打包：`mvn clean package -P prod`
3. 部署到服务器，使用 systemd 管理
4. 配置 Nginx 反向代理

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/xxx`
3. 提交更改：`git commit -m 'feat: add xxx'`
4. 推送分支：`git push origin feature/xxx`
5. 提交 Pull Request

## 许可证

[MIT](LICENSE)

## 联系方式

- 项目主页：https://github.com/hhnbhh666/e-du
- 问题反馈：https://github.com/hhnbhh666/e-du/issues

---

**温馨提示**：接口文档、数据库设计等敏感文档请单独管理，不在代码仓库中。