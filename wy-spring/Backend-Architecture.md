# 后端架构设计文档

## 一、技术栈

| 层级 | 技术选型 | 版本 |
|-----|---------|------|
| 框架 | Spring Boot | 2.7+ |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 6.0+ |
| ORM | MyBatis | 3.5+ |
| 安全 | Spring Security + JWT | - |
| 文档 | Swagger / OpenAPI | 3.0 |
| 构建 | Maven | 3.6+ |

---

## 二、数据库设计

### 2.1 用户模块

#### 用户表 (users)

```sql
CREATE TABLE `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号，登录账号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码，bcrypt加密',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除：0正常 1删除',
  
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

**说明**:
- 用户使用手机号 + 密码登录
- 密码使用 bcrypt 加密，cost=12
- 支持软删除
- 角色区分：普通用户(user)、老师(teacher)、管理员(admin)

#### 教师表 (teachers)

```sql
CREATE TABLE `teachers` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '教师ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '教师姓名',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '职称/头衔',
  `introduction` TEXT COMMENT '个人简介',
  `subjects` VARCHAR(500) DEFAULT NULL COMMENT '教授科目，JSON数组',
  `credentials` VARCHAR(500) DEFAULT NULL COMMENT '资质证书图片，JSON数组',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0待审核 1通过 2拒绝',
  `review_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';
```

**说明**:
- 教师需要单独申请认证
- 通过审核后才能发布题目和课程
- 关联users表共享登录账号

---

### 2.2 课程模块

#### 分类表 (categories)

```sql
CREATE TABLE `categories` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标代码/类名',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景色，如#4A90E2',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序，越小越靠前',
  `type` TINYINT UNSIGNED DEFAULT 1 COMMENT '类型：1课程分类 2题目分类',
  `parent_id` INT UNSIGNED DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  KEY `idx_type_parent` (`type`, `parent_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';
```

#### 课程表 (courses)

```sql
CREATE TABLE `courses` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
  `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '副标题/简介',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `description` TEXT COMMENT '课程详细介绍',
  `category_id` INT UNSIGNED NOT NULL COMMENT '分类ID',
  `teacher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '讲师ID',
  `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '现价',
  `original_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价',
  `episodes_count` INT UNSIGNED DEFAULT 0 COMMENT '总集数',
  `students_count` INT UNSIGNED DEFAULT 0 COMMENT '学员数量',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1上架 0下架',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序权重',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,
  
  KEY `idx_category` (`category_id`),
  KEY `idx_status_sort` (`status`, `sort_order`),
  KEY `idx_price` (`price`),
  FULLTEXT KEY `ft_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';
```

#### 课程章节表 (chapters)

```sql
CREATE TABLE `chapters` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `course_id` BIGINT UNSIGNED NOT NULL COMMENT '所属课程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '视频播放地址',
  `duration` INT UNSIGNED DEFAULT 0 COMMENT '视频时长（秒）',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  KEY `idx_course` (`course_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';
```

#### 大学表 (universities)

```sql
CREATE TABLE `universities` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL COMMENT '大学名称',
  `logo` VARCHAR(500) DEFAULT NULL COMMENT 'Logo图片',
  `course_count` INT UNSIGNED DEFAULT 0 COMMENT '课程数量',
  `sort_order` INT UNSIGNED DEFAULT 0,
  `status` TINYINT UNSIGNED DEFAULT 1,
  
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大学表';
```

---

### 2.3 刷题模块

#### 题目表 (questions)

```sql
CREATE TABLE `questions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `content` TEXT NOT NULL COMMENT '题目内容',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '题目配图',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '讲解视频',
  `type` TINYINT UNSIGNED DEFAULT 1 COMMENT '题型：1单选 2多选 3判断',
  `category_id` INT UNSIGNED NOT NULL COMMENT '所属分类',
  `teacher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '出题老师ID',
  `tip` TEXT COMMENT '答题技巧/口诀，支持富文本HTML',
  `explanation` LONGTEXT COMMENT '详细解析，支持富文本HTML',
  `correct_answer` TINYINT UNSIGNED NOT NULL COMMENT '正确答案索引：0=A,1=B...',
  `answer_count` INT UNSIGNED DEFAULT 0 COMMENT '被答次数统计',
  `correct_count` INT UNSIGNED DEFAULT 0 COMMENT '答对次数统计',
  `difficulty` TINYINT UNSIGNED DEFAULT 2 COMMENT '难度：1简单 2中等 3困难',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝 3已下架',
  `review_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,
  
  KEY `idx_category` (`category_id`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_type` (`type`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';
```

**说明**:
- 去掉了答题用时字段
- `tip` 和 `explanation` 支持富文本HTML格式
- 添加 `teacher_id` 关联出题老师
- 添加审核状态：0待审核 1已通过 2已拒绝 3已下架
- 老师添加的题目需要审核后才能上架

#### 题目选项表 (question_options)

```sql
CREATE TABLE `question_options` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '所属题目ID',
  `letter` VARCHAR(10) NOT NULL COMMENT '选项字母：A/B/C/D',
  `content` VARCHAR(500) NOT NULL COMMENT '选项内容',
  `sort_order` TINYINT UNSIGNED DEFAULT 0 COMMENT '排序',
  
  KEY `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';
```

#### 用户答题记录表 (user_answer_records)

```sql
CREATE TABLE `user_answer_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  `selected_option` TINYINT UNSIGNED NOT NULL COMMENT '选择的选项索引',
  `is_correct` TINYINT UNSIGNED NOT NULL COMMENT '是否正确：1正确 0错误',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  
  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_correct` (`is_correct`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录表';
```

**说明**: 去掉了答题用时字段。

#### 收藏题目表 (favorite_questions)

```sql
CREATE TABLE `favorite_questions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  
  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏题目表';
```

---

### 2.4 评论模块

#### 评论表 (comments)

```sql
CREATE TABLE `comments` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
  `target_type` TINYINT UNSIGNED NOT NULL COMMENT '对象类型：1题目 2课程',
  `target_id` BIGINT UNSIGNED NOT NULL COMMENT '对象ID',
  `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父评论ID，0为顶级评论',
  `likes` INT UNSIGNED DEFAULT 0 COMMENT '点赞数',
  `is_author` TINYINT UNSIGNED DEFAULT 0 COMMENT '是否官方/讲师回复：1是 0否',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1正常 0屏蔽',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,
  
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

---

### 2.5 OCR识别与试卷模块

#### OCR识别记录表 (ocr_records)

```sql
CREATE TABLE `ocr_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `teacher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '老师ID',
  `original_image` VARCHAR(500) NOT NULL COMMENT '原始上传图片URL',
  `ocr_text` LONGTEXT COMMENT 'OCR识别的原始文本',
  `parsed_questions` JSON DEFAULT NULL COMMENT '解析后的题目JSON',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0处理中 1成功 2失败',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  
  KEY `idx_user` (`user_id`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OCR识别记录表';
```

**说明**:
- 存储OCR识别的原始图片和识别结果
- `parsed_questions` 存储结构化后的题目数据
- 支持老师批量导入试卷

#### 试卷表 (exam_papers)

```sql
CREATE TABLE `exam_papers` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL COMMENT '试卷标题',
  `teacher_id` BIGINT UNSIGNED NOT NULL COMMENT '创建老师ID',
  `category_id` INT UNSIGNED NOT NULL COMMENT '所属分类',
  `description` TEXT COMMENT '试卷描述',
  `total_questions` INT UNSIGNED DEFAULT 0 COMMENT '总题数',
  `total_score` INT UNSIGNED DEFAULT 100 COMMENT '总分',
  `time_limit` INT UNSIGNED DEFAULT 0 COMMENT '时间限制（分钟），0为不限',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
  `word_url` VARCHAR(500) DEFAULT NULL COMMENT '导出的Word文档URL',
  `pdf_url` VARCHAR(500) DEFAULT NULL COMMENT '导出的PDF文档URL',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0草稿 1已发布 2已下架',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,
  
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';
```

#### 试卷题目关联表 (paper_questions)

```sql
CREATE TABLE `paper_questions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `paper_id` BIGINT UNSIGNED NOT NULL COMMENT '试卷ID',
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  `score` INT UNSIGNED DEFAULT 1 COMMENT '该题分值',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序',
  
  UNIQUE KEY `uk_paper_question` (`paper_id`, `question_id`),
  KEY `idx_paper` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';
```

---

## 三、实体关系图 (ER)

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   users     │       │   courses   │       │   chapters  │
│  (用户表)    │       │  (课程表)    │       │  (章节表)    │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ PK id       │       │ PK id       │       │ PK id       │
│ phone       │       │ FK category_id│◄────│ FK course_id │
│ password    │       │ title       │       │ title       │
│ nickname    │       │ price       │       │ video_url   │
│ avatar      │       │ students_count│      │ duration    │
└──────┬──────┘       └─────────────┘       └─────────────┘
       │
       │               ┌─────────────┐       ┌─────────────┐
       │               │ categories│       │universities │
       │               │ (分类表)     │       │  (大学表)    │
       │               ├─────────────┤       ├─────────────┤
       │               │ PK id       │       │ PK id       │
       │               │ name        │       │ name        │
       │               │ icon        │       │ logo        │
       │               │ bg_color    │       │ course_count│
       │               └─────────────┘       └─────────────┘
       │
       │◄──────────────┐
       │               │
┌──────▼──────┐       │       ┌─────────────┐
│user_answer_ │       │       │  questions  │
│  records    │       │       │  (题目表)    │
│ (答题记录)   │       │       ├─────────────┤
├─────────────┤       │       │ PK id       │
│ PK id       │       │       │ FK category_id│
│ FK user_id  │       │       │ content     │
│ FK question_id│◄────┘       │ image_url   │
│ selected_option│            │ correct_answer│
│ is_correct  │              │ tip         │
└─────────────┘              │ explanation│
                             └─────────────┘
                                    │
┌─────────────┐                   │
│favorite_    │                   │       ┌─────────────┐
│  questions  │                   └──────►│question_    │
│ (收藏表)     │                           │  options    │
├─────────────┤                           │ (选项表)     │
│ PK id       │                           ├─────────────┤
│ FK user_id  │◄─────────────────────────│ PK id       │
│ FK question_id│                         │ FK question_id│
└─────────────┘                           │ letter      │
                                          │ content     │
┌─────────────┐                           └─────────────┘
│  comments   │
│  (评论表)   │
├─────────────┤
│ PK id       │
│ FK user_id  │
│ target_type │
│ target_id   │
│ content     │
│ parent_id   │
│ likes       │
└─────────────┘
       │
       │         ┌─────────────┐       ┌─────────────┐
       │         │  teachers   │       │ocr_records│
       │         │  (教师表)   │       │(OCR记录)   │
       │         ├─────────────┤       ├─────────────┤
       │         │ PK id       │       │ PK id       │
       │         │ FK user_id  │       │ FK user_id  │
       │         │ name        │       │ image       │
       │         │ title       │       │ ocr_text    │
       │         │ status      │       │ status      │
       │         └──────┬──────┘       └─────────────┘
       │                │
       │                │◄────────────────┐
       │                │                 │
       │         ┌──────▼──────┐         │       ┌─────────────┐
       │         │  questions│         │       │ exam_papers │
       │         │  (题目表)   │         │       │  (试卷表)    │
       │         ├─────────────┤         │       ├─────────────┤
       │         │ PK id       │         │       │ PK id       │
       │◄────────│ FK teacher_id│         │       │ FK teacher_id│
       │         │ content     │         │       │ title       │
       │         │ explanation │         │       │ word_url    │
       │         │ status      │         │       │ status      │
       │         └──────┬──────┘         │       └──────┬──────┘
       │                │                │              │
       │                │◄───────────────┘              │
       │                │                               │
       │         ┌──────▼──────┐                  ┌──────▼──────┐
       │         │paper_       │                  │paper_       │
       │         │ questions   │                  │ questions   │
       │         │(试卷题目关联)│                  │(试卷题目关联)│
       │         ├─────────────┤                  ├─────────────┤
       │         │ PK id       │                  │ PK id       │
       └────────►│ FK question_id│                │ FK paper_id │
                 │ FK paper_id │                  │ FK question_id│
                 │ score       │                  │ score       │
                 └─────────────┘                  └─────────────┘
```

**实体关系说明**:

| 关系 | 类型 | 说明 |
|-----|------|------|
| users → teachers | 1:1 | 一个用户可以成为老师 |
| teachers → questions | 1:N | 一个老师可以出多道题目 |
| teachers → exam_papers | 1:N | 一个老师可以创建多份试卷 |
| users → ocr_records | 1:N | 一个用户可以有多条OCR识别记录 |
| exam_papers → paper_questions | 1:N | 一份试卷包含多道题目 |
| questions → paper_questions | 1:N | 一道题目可以属于多份试卷 |

---

## 四、项目结构

```
com.example.wy
├── WyApplication.java              // 启动类
├──
├── controller                      // 控制层
│   ├── AuthController.java          // 认证接口
│   ├── UserController.java          // 用户接口
│   ├── CourseController.java        // 课程接口
│   ├── QuestionController.java      // 刷题接口（学生端）
│   ├── TeacherQuestionController.java // 题目管理接口（老师端）
│   ├── TeacherPaperController.java    // 试卷管理接口（老师端）
│   ├── OcrController.java           // OCR识别接口
│   ├── CommentController.java       // 评论接口
│   └── UploadController.java        // 文件上传接口
│
├── service                         // 业务层
│   ├── UserService.java
│   ├── CourseService.java
│   ├── QuestionService.java
│   ├── TeacherQuestionService.java   // 老师题目管理
│   ├── ExamPaperService.java         // 试卷服务
│   ├── OcrService.java               // OCR识别服务
│   ├── CommentService.java
│   └── impl                        // 实现类
│       ├── UserServiceImpl.java
│       ├── CourseServiceImpl.java
│       ├── QuestionServiceImpl.java
│       ├── TeacherQuestionServiceImpl.java
│       ├── ExamPaperServiceImpl.java
│       ├── OcrServiceImpl.java
│       └── CommentServiceImpl.java
│
├── mapper                          // 数据访问层
│   ├── UserMapper.java
│   ├── TeacherMapper.java
│   ├── CourseMapper.java
│   ├── QuestionMapper.java
│   ├── QuestionOptionMapper.java
│   ├── UserAnswerRecordMapper.java
│   ├── FavoriteQuestionMapper.java
│   ├── CommentMapper.java
│   ├── OcrRecordMapper.java
│   ├── ExamPaperMapper.java
│   └── PaperQuestionMapper.java
│
├── entity                          // 实体类
│   ├── User.java
│   ├── Teacher.java
│   ├── Course.java
│   ├── Chapter.java
│   ├── Category.java
│   ├── University.java
│   ├── Question.java
│   ├── QuestionOption.java
│   ├── UserAnswerRecord.java
│   ├── FavoriteQuestion.java
│   ├── OcrRecord.java
│   ├── ExamPaper.java
│   └── PaperQuestion.java
│   └── Comment.java
│
├── dto                             // 数据传输对象
│   ├── request                     // 请求参数
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CourseQueryRequest.java
│   │   ├── AnswerSubmitRequest.java
│   │   └── CommentCreateRequest.java
│   │
│   └── response                    // 响应数据
│       ├── LoginResponse.java
│       ├── CourseListResponse.java
│       ├── CourseDetailResponse.java
│       ├── QuestionListResponse.java
│       ├── AnswerResultResponse.java
│       └── CommentListResponse.java
│
├── vo                              // 视图对象
│   ├── CourseVO.java
│   ├── QuestionVO.java
│   └── CommentVO.java
│
├── config                          // 配置类
│   ├── SecurityConfig.java         // 安全配置
│   ├── JwtConfig.java              // JWT配置
│   ├── RedisConfig.java            // Redis配置
│   └── MyBatisConfig.java          // MyBatis配置
│
├── utils                           // 工具类
│   ├── JwtUtils.java               // JWT工具
│   ├── BCryptUtils.java            // 加密工具
│   └── DateUtils.java              // 日期工具
│
├── enums                           // 枚举类
│   ├── ErrorCode.java              // 错误码
│   ├── UserStatus.java             // 用户状态
│   ├── CourseStatus.java           // 课程状态
│   └── QuestionType.java           // 题目类型
│
├── exception                       // 异常处理
│   ├── BusinessException.java      // 业务异常
│   └── GlobalExceptionHandler.java // 全局异常处理
│
└── interceptor                     // 拦截器
    └── JwtInterceptor.java         // JWT认证拦截
```

---

## 五、核心业务流程

### 5.1 登录流程

```
用户输入手机号+密码
      ↓
Controller接收参数
      ↓
Service验证手机号格式
      ↓
Mapper根据手机号查询用户
      ↓
BCrypt验证密码
      ↓
生成JWT Token
      ↓
返回Token+用户信息
```

### 5.2 答题流程

```
用户选择答案
      ↓
提交题目ID+选项索引
      ↓
查询题目正确答案
      ↓
比对判断是否答对
      ↓
保存/更新答题记录（无答题用时）
      ↓
更新题目统计（答对数/答错数）
      ↓
返回答题结果+解析+技巧
```

**限制条件**:
- 只有答过题的用户才能看到解析和评论
- 只有答过题的用户才能发表评论

### 5.3 评论流程

```
用户提交评论内容
      ↓
检查用户是否答过该题
      ↓
检查评论内容（1-1000字）
      ↓
保存评论
      ↓
返回新评论数据
```

---

## 六、缓存策略

| 数据类型 | 缓存Key | 过期时间 | 更新策略 |
|---------|--------|---------|---------|
| 用户Token | `token:{userId}` | 24小时 | 登录刷新，登出删除 |
| 用户信息 | `user:{userId}` | 1小时 | 更新时删除 |
| 课程列表 | `courses:{category}:{page}` | 10分钟 | 定时刷新 |
| 课程详情 | `course:{courseId}` | 30分钟 | 更新时删除 |
| 题目详情 | `question:{questionId}` | 30分钟 | 更新时删除 |
| 评论列表 | `comments:{targetType}:{targetId}:{sort}` | 5分钟 | 新增评论时删除 |
| 排行榜 | `ranking:{type}` | 5分钟 | 定时刷新 |
