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
- Redis 6.0+

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
    password: your_password

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
- `GET /api/auth/me` - 获取当前用户信息

### 刷题接口
- `GET /api/questions` - 获取题目列表
- `GET /api/questions/{id}` - 获取题目详情
- `POST /api/questions/{id}/answer` - 提交答题结果
- `POST /api/questions/{id}/favorite` - 收藏/取消收藏题目

### 分类接口
- `GET /api/categories` - 获取分类列表

## 开发规范

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

## 下一步开发计划

1. 实现课程模块接口
2. 实现评论模块接口
3. 实现老师端题目管理接口
4. 实现试卷管理接口
5. 实现OCR识别接口
6. 添加文件上传功能
