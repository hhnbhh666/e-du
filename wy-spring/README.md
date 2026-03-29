# wy-spring 后端服务

网易云课堂后端服务，基于 Spring Boot 2.7+ 开发。

## 技术栈

| 层级  | 技术选型                  |
|-----|-----------------------|
| 框架  | Spring Boot 2.7.18    |
| 数据库 | MySQL 8.0             |
| 缓存  | Redis 6.0+            |
| ORM | MyBatis-Plus 3.5+     |
| 安全  | Spring Security + JWT |
| 文档  | Swagger / OpenAPI 3.0 |

## 项目结构

```
org.example.wy
├── WySpringApplication.java       // 启动类
├── controller                     // 控制层
├── service                        // 业务层
│   └── impl                       // 实现类
├── mapper                         // 数据访问层（MyBatis）
├── entity                         // 实体类
├── dto                            // 数据传输对象
│   ├── request                    // 请求参数
│   └── response                   // 响应数据
├── vo                             // 视图对象
├── config                         // 配置类
├── utils                          // 工具类
├── enums                          // 枚举类
├── exception                      // 异常处理
└── interceptor                    // 拦截器
```

## 快速开始

### 1. 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+（**须先启动**，短信验证码、滑动校验令牌、频控都依赖 Redis；未启动会报 Redis 相关错误或「系统内部错误」）

**本地启动 Redis 示例**

- Windows：安装后执行 `redis-server`（或对应服务的「启动」）。
- Docker：`docker run -d --name wy-redis -p 6379:6379 redis:7`
- 密码与 `application.yml` 中 `spring.redis.password`（可用环境变量 `REDIS_PASSWORD`）保持一致；本地无密码时不要配置 `requirepass`。

### 2. 数据库初始化

```bash
# 创建数据库并执行初始化脚本（建表 + 题目分类 type=2）
mysql -u root -p < database.sql

# 可选：导入测试数据（课程分类、默认用户/教师、5 道题目及选项），便于前端联调
mysql -u root -p wy_edu < data-test.sql
```

### 3. 配置文件

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wy_edu?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD:}   # 无密码留空；有密码用环境变量注入

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时
```

### 4. 启动项目

```bash
# 开发模式启动
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/wy-spring-0.0.1-SNAPSHOT.jar
```

### 5. 访问接口文档

启动后访问 Swagger UI：
- http://localhost:8080/swagger-ui.html

## 已实现的接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/slide/start` - 滑动验证开始，返回 `slideId`
- `POST /api/auth/slide/verify` - 滑动完成， body: `slideId`, `durationMs`, `progress`(0–100)，返回一次性 `gateToken`
- `POST /api/auth/sms/send` - 发送登录短信，body: `phone`, `gateToken`（需 Redis；`app.sms.mock-enabled=true` 时验证码仅打日志）
- `POST /api/auth/sms/login` - 短信登录，新用户自动注册
- `GET /api/auth/me` - 获取当前用户信息

### 刷题接口
- `GET /api/questions` - 获取题目列表
- `GET /api/questions/{id}` - 获取题目详情
- `POST /api/questions/{id}/answer` - 提交答题结果
- `POST /api/questions/{id}/favorite` - 收藏/取消收藏题目

### 分类接口
- `GET /api/categories` - 获取分类列表

### 教师管理（需 `Authorization: Bearer <token>`）
**老师端**
- `POST /api/v1/teacher/apply` - 申请成为教师；`body`: `name`, `title?`, `introduction?`, `subjects?`, `avatar?`, `credentials?`
- `GET /api/v1/teacher/me` - 当前用户教师档案（未申请则 `data` 为 `null`）
- `PUT /api/v1/teacher/me` - 更新本人资料（待审核/已通过；已拒绝需重新 `apply`）

**后台审核**
- `GET /api/v1/admin/teachers` - 分页列表，`query`: `page`, `size`, `status?`(0/1/2), `keyword?`（姓名或手机号）
- `GET /api/v1/admin/teachers/{id}` - 详情
- `POST /api/v1/admin/teachers/{id}/review` - 审核，`body`: `{ "approve": true/false, "reviewReason": "拒绝时填写" }`（仅待审核）
- `DELETE /api/v1/admin/teachers/{id}` - 逻辑删除

若库中 `teachers` 表无 `is_deleted` 字段，执行 `database-migration-teachers-is-deleted.sql`。

## 开发规范

仓库级约定（前端 uni-app、Git、协作等）见 **[../docs/CODING-STANDARDS.md](../docs/CODING-STANDARDS.md)**。本节仅列本服务 API 约定。

### 响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 错误码

| 错误码 | 说明 |
|-------|------|
| 200   | 成功 |
| 1xxx  | 通用错误 |
| 2xxx  | 用户错误 |
| 3xxx  | 教师错误 |
| 4xxx  | 课程错误 |
| 5xxx  | 题目错误 |
| 6xxx  | 评论错误 |
| 7xxx  | 文件上传错误 |
| 8xxx  | OCR错误 |
| 9xxx  | 系统错误 |

## 百度 OCR（试卷扫描识别）

仓库内 `application.yml` 使用**环境变量**占位，避免密钥进 Git。在 [百度智能云](https://console.bce.baidu.com/ai/) 创建应用后，本地启动前设置（PowerShell 示例）：

```powershell
$env:BAIDU_OCR_ENABLED="true"
$env:BAIDU_OCR_API_KEY="你的 API Key"
$env:BAIDU_OCR_SECRET_KEY="你的 Secret Key"
mvn spring-boot:run
```

未启用或未配置时，`POST /api/v1/teacher/ocr/recognize` 会返回明确错误提示；启用后由服务端调用百度「通用文字识别」再按版式拆题（需人工校对）。

## 下一步开发计划

1. 实现课程模块接口
2. 实现评论模块接口
3. 实现老师端题目管理接口
4. 实现试卷管理接口
5. ~~实现OCR识别接口~~（已接入百度通用 OCR + 拆题）
6. 添加文件上传功能
