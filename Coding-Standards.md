# 网易云课堂 - 代码规范

## 一、前端规范 (wy-uni)

### 1.1 Vue 文件规范

#### 模板部分
- 使用 4 空格缩进
- 标签属性换行时对齐
- 条件渲染使用 `v-if`，列表渲染使用 `v-for`
- 动态 class 使用对象语法，多条件时每个条件一行

```vue
<view 
    class="option-item"
    :class="{ 
        selected: selectedOption === index,
        correct: hasAnswered && index === Number(currentQuestion.correctAnswer),
        wrong: hasAnswered && selectedOption === index && index !== Number(currentQuestion.correctAnswer)
    }"
    @click="selectOption(index)"
>
```

#### Script 部分
- 使用 Vue3 `<script setup>` 语法
- 响应式数据使用 `ref()`
- 禁止 TypeScript 类型注解（项目使用纯 JavaScript）
- 所有 `showLoading` 必须有对应的 `hideLoading`（使用 try-finally）

```javascript
// 正确示例
async function loadQuestions() {
    uni.showLoading({ title: '加载中...' })
    try {
        const res = await questionApi.getQuestionList({ page: 1, size: 20 })
        questionList.value = res?.list || []
    } catch (e) {
        console.warn('加载失败', e)
    } finally {
        uni.hideLoading()
    }
}
```

#### API 调用规范
- 统一从 `@/api/index.js` 导入
- 响应数据使用可选链操作符 `?.` 防止 undefined 错误
- 类型转换显式使用 `Number()`

```javascript
const res = await questionApi.submitAnswer(currentQuestion.value.id, index)
isCorrect.value = res.correct === true
currentQuestion.value.correctAnswer = Number(res.correctAnswer)
```

### 1.2 样式规范
- 使用 rpx 作为单位
- 颜色使用十六进制，推荐使用主题色变量
- 嵌套层级不超过 4 层

### 1.3 命名规范
- 组件名：PascalCase
- 变量/函数：camelCase
- 常量：UPPER_SNAKE_CASE
- 布尔值前缀：is/has/show

### 1.4 老师端页面路由（题目与试卷）
- **聚合页**：`pages/teacher/teacher-manage` — 含「手动新增题目」「扫描导入试卷」两个入口。
- **子页**：`add-question`（单题录入）、`doc-import`（OCR 试卷识别导入）。
- **主入口**：底部导航 **「找课」**（`index` / `quiz` / `teacher-manage` 内联底栏一致）。老师角色下点击「找课」进入 `teacher-manage`；刷题页不再单独放老师入口卡片。

---

## 二、后端规范 (wy-spring)

### 2.1 代码结构
```
org.example.wyspring
├── controller     // 控制层：参数校验、调用 service
├── service        // 业务层：业务逻辑
│   └── impl       // 实现类
├── mapper         // 数据访问层（MyBatis）
├── entity         // 实体类：对应数据库表
├── dto            // 数据传输对象
│   ├── request    // 请求参数（含校验注解）
    └── response   // 响应数据
├── vo             // 视图对象：返回给前端的格式
├── config         // 配置类
├── utils          // 工具类
├── enums          // 枚举类
├── exception      // 异常处理
└── interceptor    // 拦截器
```

### 2.2 Controller 规范

#### 请求映射
- 类上使用 `@RequestMapping("/api/xxx")`
- 方法使用具体 HTTP 方法注解：`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- 使用 Swagger 注解 `@Operation` 描述接口

```java
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "刷题接口")
public class QuestionController {
    
    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情")
    public Result<QuestionVO> detail(@PathVariable Long id) {
        // ...
    }
}
```

#### 参数校验
- 请求 DTO 使用 `javax.validation` 注解
- 方法参数使用 `@Valid` 触发校验

```java
@PostMapping
public Result<Long> create(@RequestBody @Valid QuestionCreateRequest request) {
    // ...
}
```

### 2.3 Service 规范

#### 事务管理
- 写操作使用 `@Transactional`
- 批量操作考虑事务回滚

#### VO 转换
- 使用私有方法 `convertToVO()` 统一转换
- 敏感字段控制：答题后才返回 `correctAnswer`
- 避免循环查询数据库

```java
private QuestionVO convertToVO(Question question, Long userId, boolean showAnswer) {
    QuestionVO vo = new QuestionVO();
    vo.setId(question.getId());
    // 选项转换
    if (question.getOptions() != null) {
        vo.setOptions(question.getOptions().stream()
            .map(opt -> {
                QuestionVO.OptionVO optionVO = new QuestionVO.OptionVO();
                optionVO.setLetter(opt.getLetter());
                optionVO.setContent(opt.getContent());
                return optionVO;
            }).collect(Collectors.toList()));
    }
    // 答题后才显示答案
    if (showAnswer || (userId != null && hasAnswered(userId, question.getId()))) {
        vo.setCorrectAnswer(question.getCorrectAnswer());
        vo.setTip(question.getTip());
        vo.setExplanation(question.getExplanation());
    }
    return vo;
}
```

### 2.4 命名规范

| 类型 | 命名规则 | 示例 |
|-----|---------|------|
| 类名 | PascalCase | QuestionService |
| 方法 | camelCase | getQuestionDetail |
| 常量 | UPPER_SNAKE_CASE | MAX_PAGE_SIZE |
| 布尔字段 | 避免 is 前缀（防止序列化问题） | correct（而非 isCorrect） |

### 2.5 响应格式

统一使用 `Result<T>` 包装：
```java
{
    "code": 200,
    "message": "操作成功",
    "data": {}
}
```

### 2.6 自动重新运行

修改代码后自动重新编译运行步骤：
```bash
# 1. 停止现有 Java 进程
taskkill /F /IM java.exe 2>nul

# 2. 重新打包
cd wy-spring
mvn clean package -DskipTests -q

# 3. 启动 JAR
java -jar target/wy-spring-0.0.1-SNAPSHOT.jar
```

---

## 三、前后端对接规范

### 3.1 接口路径
- 统一前缀：`/api/`
- 版本控制：`/api/v1/`

### 3.2 字段命名
- 后端：camelCase（Java 规范）
- 前端：接收 camelCase，模板中使用小写开头

### 3.3 类型转换注意
| Java 类型 | JSON | 前端接收 | 注意事项 |
|----------|------|---------|---------|
| Integer | number | number | 可能变为 string，用 Number() 转换 |
| Long | number/string | number/string | 大数可能精度丢失 |
| Boolean | boolean | boolean | 字段名避免 is 前缀 |
| LocalDateTime | string | string | 建议格式化为 yyyy-MM-dd HH:mm:ss |

### 3.4 Boolean 字段命名
**重要**：Lombok 生成的 getter 对 `isXxx` 字段会有序列化问题。

```java
// 错误 ❌
private Boolean isCorrect;  // getter: isIsCorrect()，序列化后字段名不确定

// 正确 ✅
private Boolean correct;    // getter: getCorrect()，序列化后字段名：correct
```

---

## 四、Git 提交规范

### 4.1 提交信息格式
```
<type>: <subject>

<body>
```

### 4.2 类型说明
| 类型 | 说明 |
|-----|------|
| feat | 新功能 |
| fix | 修复 bug |
| docs | 文档修改 |
| style | 代码格式（不影响功能的变动） |
| refactor | 重构 |
| test | 测试相关 |
| chore | 构建/工具相关 |

### 4.3 示例
```
feat: 添加题目创建接口

- 实现 TeacherController.createQuestion
- 支持单选/多选/判断题
- 自动关联默认老师ID
```

---

## 五、数据库规范

### 5.1 表名与字段
- 表名：小写，下划线分隔，复数形式
- 字段名：小写，下划线分隔
- Java 实体使用 camelCase，MyBatis 自动映射

### 5.2 必备字段
```sql
id              主键
created_at      创建时间
updated_at      更新时间
is_deleted      软删除标志（逻辑删除）
```

### 5.3 测试数据
- 测试数据单独放在 `data-test.sql`
- 初始化脚本放在 `database.sql`
- 测试数据仅执行一次，避免重复插入

---

## 六、开发流程

### 6.1 新增功能步骤
1. 设计接口（API-Interface.md）
2. 后端：entity → mapper → service → controller
3. 前端：api → 页面调用
4. 联调测试
5. 代码审查（self-review）
6. 提交代码

### 6.2 Bug 修复流程
1. 定位问题（前后端日志）
2. 修复代码
3. 本地验证
4. 更新测试用例（如有）
5. 提交代码

### 6.3 代码自审清单
- [ ] 代码能通过编译（无语法错误）
- [ ] Linter 无错误
- [ ] 功能已本地测试
- [ ] 接口文档已更新（如有变更）
- [ ] 数据库脚本已更新（如有变更）
