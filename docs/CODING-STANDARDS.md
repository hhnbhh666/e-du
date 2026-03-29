# 编码与工程规范（wy 仓库）

团队统一按本文档执行，减少跨端、构建与环境问题。更细的 Java 约定见 `wy-spring` 内说明。

---

## 1. 仓库与目录

| 目录 | 说明 |
|------|------|
| `wy-uni/` | UniApp + Vue3 前端，**工程根**即本目录（与 `manifest.json` 同级） |
| `wy-spring/` | Spring Boot 后端 |
| `docs/` | 仓库级文档（含本规范） |

---

## 2. `wy-uni` 前端

### 2.1 运行与依赖

- 使用 **HBuilderX** 打开 **`wy-uni`**，不要用仓库根当 uni-app 工程根。
- 在 **`wy-uni`** 下安装依赖：`npm install`（无需 yarn）。
- **`@dcloudio/*` 的 alpha 包**：`package.json` 里**写死版本号**，**不要**加 `^` / `~`；升级 **HBuilderX** 后若控制台提示新版本号，再整体对齐 `uni-uts-v1`、`vite-plugin-uni`、`uni-cli-shared` 等同一代号。
- 可选自检：`npm run verify:uts`（确认 `uni-uts-v1` / `vite-plugin-uni` 可被解析）。

### 2.2 Vite

- 保留根目录 **`vite.config.js`**：`@dcloudio/vite-plugin-uni` + `build.rollupOptions.output.inlineDynamicImports: true`（App 端 IIFE 与动态分包冲突）。
- 不要随意改 `output.format` 或关闭 `inlineDynamicImports`，除非确认所有目标平台构建均通过。

### 2.3 脚本与模块

- **禁止**在页面/业务代码中使用 **`import()` 动态导入**（易触发 App 打包 IIFE + code-splitting 报错）。请使用顶部 **`import { xxx } from '@/api/...'`** 等静态导入。
- 公共接口统一从 **`@/api`**、`@/utils` 等路径引用，路径别名与 HBuilderX/uni 配置保持一致。

### 2.4 样式（尤其微信小程序）

- **全局样式**（如 `App.vue`）中**不要使用** CSS 通用选择器 **`*`**；小程序 **WXSS 不支持**。
- 勿在小程序端使用 **`::-webkit-scrollbar`** 等仅 H5 有效的选择器；需区分端时用 `/* #ifdef H5 */`。

### 2.5 Vue 单文件组件

- 使用 **Tab** 缩进（与现有页面一致）。
- `template` / `script` / `style` 顺序与项目现有文件保持一致。

### 2.6 地图与 Key

- 地图页使用 **`<map>`** 全端统一实现；密钥与平台说明见 **`manifest.json`** 注释及 `README.md`。
- 勿再引入仅 H5 可用的境外 OSM 嵌入方案作为主路径（维护成本与网络环境风险高）。

---

## 3. `wy-spring` 后端

- JDK 8+、Maven；配置以 **`application.yml`** 为准，敏感信息勿提交仓库。
- 接口风格、包结构、命名与现有 Controller/Service 保持一致；新增模块先读同目录示例再写。

---

## 4. Git 与协作

- **`node_modules/`** 不提交；克隆后在 **`wy-uni`** 执行 `npm install`。
- 大段实验性脚本、个人环境路径说明不要长期留在仓库根；可写入 **`docs/`** 或 issue/笔记。
- **唯一规范来源**：`docs/CODING-STANDARDS.md`（不要在仓库根另起一份同名文档，避免分叉）。

---

## 5. 修订记录

- 2026-03：合并 uni-app 构建踩坑结论（锁版本、静态导入、WXSS、Vite IIFE）；删除临时 HBX 同步脚本后，规范仍以「工程内 `npm install` + 上述约束」为主。
