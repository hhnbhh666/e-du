---
name: "project-rules"
description: "Project development rules and conventions. Invoke when making code changes, especially backend updates that require server restart."
---

# 项目开发规范

## 后端更新规范

### 自动重启规则

**重要：每次修改后端代码后，必须重新编译并重启后端服务！**

当进行以下操作时，必须重启后端：
1. 修改 Java 代码（Entity、Service、Controller、Mapper 等）
2. 修改配置文件（application.yml、pom.xml 等）
3. 添加新的依赖
4. 修改数据库相关代码

### 重启命令

```bash
# 1. 停止占用端口的进程
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object OwningProcess | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }

# 2. 编译打包
cd c:\code\wy\wy-spring
mvn clean package -DskipTests -q

# 3. 启动后端
java -jar target/wy-spring-0.0.1-SNAPSHOT.jar
```

### 一键重启命令

```bash
cd c:\code\wy\wy-spring; Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object OwningProcess | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force } 2>$null; Start-Sleep -Seconds 2; mvn clean package -DskipTests -q; java -jar target/wy-spring-0.0.1-SNAPSHOT.jar
```

## 前端开发规范

### UniApp 页面开发

1. 使用 Vue 3 Composition API (`<script setup>`)
2. 样式使用 `scoped` 避免污染
3. API 调用统一使用 `@/api/` 目录下的模块

### 底部导航配置

当前底部导航顺序：
1. 首页
2. 找课
3. 社区
4. 直播
5. 我

## 数据库规范

### 直播模块表结构

- `live_rooms` - 直播间表
- `live_danmaku` - 弹幕表
- `live_products` - 直播商品表
- `live_viewer_records` - 观众记录表
- `live_categories` - 直播分类表

### 初始化脚本

数据库初始化脚本位于：`c:\code\wy\wy-spring\database-live.sql`

## 环境配置

- Java 版本：21
- Redis 密码：phq517762
- 后端端口：8080

## Git 提交规范

### 不需要提交的文件

以下文件/目录**不需要提交到 Git**：

1. **文档文件**：所有 `.md` 文件（README.md、开发文档等）
2. **Skill 文件**：`.trae/skills/` 目录下的所有文件
3. **配置说明**：项目规范、开发指南等辅助文档

### 提交前检查

```bash
# 查看待提交文件
git status

# 如果误添加了 .md 或 skill 文件，使用以下命令移除
git reset HEAD -- <file-path>
```
