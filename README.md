# 网易云课堂 - 教育类 APP

一个仿网易云课堂布局的跨平台教育应用，包含课程学习和驾考刷题两大核心模块。

**编码与工程约定**：见 **[docs/CODING-STANDARDS.md](docs/CODING-STANDARDS.md)**（uni-app 依赖版本、禁止动态 `import()`、WXSS、Vite 等）。

## 项目结构

```
.
├── docs/
│   └── CODING-STANDARDS.md   # 仓库统一规范
├── wy-uni/                   # UniApp 前端（工程根 = 本目录）
│   ├── pages/
│   │   ├── index/            # 首页/找课
│   │   ├── quiz/             # 刷题（学生端）
│   │   ├── map/              # 地图
│   │   └── teacher/          # 老师端
│   └── static/
└── wy-spring/                # Spring Boot 后端
```

## 技术栈

### 前端
- **框架**: UniApp + Vue3 + Vite（`vite.config.js` + `@dcloudio/vite-plugin-uni`）
- **UI**: 自定义组件 + 原生样式
- **跨平台**: 微信小程序、H5、App

### 后端
- **框架**: Spring Boot 2.7+、MySQL、MyBatis、Redis、JWT 等（详见 `wy-spring/README.md`）

## 快速开始

### 前端（wy-uni）

1. 安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)。
2. **导入** 目录 **`wy-uni`**（与 `manifest.json` 同级）。
3. 终端执行：
   ```bash
   cd wy-uni
   npm install
   ```
   可选：`npm run verify:uts` 检查 `@dcloudio` 依赖是否可被 Node 解析。
4. **运行** 到浏览器 / 微信开发者工具 / 手机。

升级 HBuilderX 后若编译提示 **`@dcloudio/uni-uts-v1` 等版本号**，按提示把 `package.json` 里相关 **`@dcloudio/*` 锁到同一版本**（勿加 `^`），再 `npm install`。异常时可删除 **`wy-uni/unpackage`** 后重试。

**联调后端**：修改 `wy-uni/utils/request.js` 中的 `API_BASE_URL`；真机勿用 `localhost`，改用电脑局域网 IP。

### 后端（wy-spring）

```bash
cd wy-spring
mvn spring-boot:run
```

需先建库、执行 SQL、配置 `application.yml`。详见 `wy-spring/README.md`。

## 地理位置与地图（摘要）

- 封装：`wy-uni/utils/location.js`。
- **微信小程序**：`manifest.json` 已配置位置权限相关项。
- **App**：Android 定位权限与 iOS 说明文案已在 manifest 中配置。
- **地图页**：`pages/map/map`，全端 **`map` 组件**；密钥见 `manifest.json`（腾讯等）。

## 环境要求

- Node.js 18+（用于 `wy-uni` 下 `npm install`）
- HBuilderX（推荐）
- 微信开发者工具（小程序）
- 后端：JDK 8+、Maven、MySQL、Redis
