# 代码编写规范与习惯

## 项目结构规范

```
com.example.wy
├── controller          // 控制层：处理HTTP请求，调用Service
├── service            // 业务层：处理业务逻辑
│   └── impl           // 实现类
├── mapper             // 数据访问层：MyBatis接口
├── entity             // 实体类：对应数据库表
├── dto                // 数据传输对象：请求/响应参数
├── vo                 // 视图对象：返回给前端的特定结构
├── config             // 配置类
├── utils              // 工具类
├── enums              // 枚举类
└── exception          // 异常处理
```

## 命名规范

### 类名
| 类型 | 命名规则 | 示例 |
|-----|---------|------|
| Controller | `XxxController` | `CourseController`, `QuestionController` |
| Service接口 | `XxxService` | `UserService`, `QuizService` |
| Service实现 | `XxxServiceImpl` | `UserServiceImpl` |
| Mapper | `XxxMapper` | `QuestionMapper`, `CommentMapper` |
| Entity | `Xxx` (对应表名) | `User`, `Question`, `Course` |
| DTO | `XxxRequest` / `XxxResponse` | `LoginRequest`, `PageResponse` |
| VO | `XxxVO` | `CourseVO`, `QuestionVO` |
| Utils | `XxxUtils` / `XxxUtil` | `JwtUtils`, `DateUtil` |
| Config | `XxxConfig` | `SecurityConfig`, `RedisConfig` |

### 方法名
- **查询**: `getXxx`, `findXxx`, `selectXxx`
- **列表**: `listXxx`, `getXxxList`
- **分页**: `getXxxPage`
- **创建**: `createXxx`, `addXxx`, `saveXxx`
- **更新**: `updateXxx`, `modifyXxx`
- **删除**: `deleteXxx`, `removeXxx`
- **验证**: `validateXxx`, `checkXxx`
- **转换**: `convertToXxx`, `toXxxVO`

### 变量名
```java
// 类成员变量使用小驼峰
private Long userId;
private String phoneNumber;
private List<Course> courseList;

// 常量使用大写下划线
private static final int MAX_RETRY_COUNT = 3;
private static final String DEFAULT_AVATAR = "...";

// 方法参数使用小驼峰
public User getUserById(Long userId) { }

// 局部变量使用小驼峰
List<Question> questions = questionMapper.selectList();
```

## 代码编写习惯

### 1. Controller 层规范
```java
@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "课程接口")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    // GET 请求用于查询
    @GetMapping
    @Operation(summary = "获取课程列表")
    public Result<PageResult<CourseVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer categoryId) {
        return Result.success(courseService.getCoursePage(page, size, categoryId));
    }
    
    // GET /{id} 用于详情
    @GetMapping("/{id}")
    @Operation(summary = "课程详情")
    public Result<CourseDetailVO> detail(@PathVariable Long id) {
        return Result.success(courseService.getCourseDetail(id));
    }
    
    // POST 用于创建
    @PostMapping
    @Operation(summary = "创建课程")
    public Result<Long> create(@RequestBody @Valid CourseCreateRequest request) {
        return Result.success(courseService.createCourse(request));
    }
    
    // PUT 用于更新
    @PutMapping("/{id}")
    @Operation(summary = "更新课程")
    public Result<Void> update(@PathVariable Long id, 
                                @RequestBody @Valid CourseUpdateRequest request) {
        courseService.updateCourse(id, request);
        return Result.success();
    }
    
    // DELETE 用于删除
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }
}
```

### 2. Service 层规范
```java
@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private UserAnswerRecordMapper recordMapper;
    
    @Override
    @Transactional
    public AnswerResultVO submitAnswer(Long userId, Long questionId, Integer selectedOption) {
        // 1. 查询题目
        Question question = questionMapper.selectById(questionId);
        if (question == null || question.getStatus() != 1) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }
        
        // 2. 判断是否已答过
        UserAnswerRecord existing = recordMapper.selectByUserAndQuestion(userId, questionId);
        boolean isCorrect = selectedOption.equals(question.getCorrectAnswer());
        
        // 3. 保存/更新记录
        UserAnswerRecord record = new UserAnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setSelectedOption(selectedOption);
        record.setIsCorrect(isCorrect ? 1 : 0);
        
        if (existing == null) {
            recordMapper.insert(record);
            // 更新题目统计
            questionMapper.updateStats(questionId, isCorrect);
        } else {
            record.setId(existing.getId());
            recordMapper.update(record);
        }
        
        // 4. 记录日志
        log.info("[QUIZ_ANSWER] userId={}, questionId={}, isCorrect={}", 
                 userId, questionId, isCorrect);
        
        // 5. 组装返回
        AnswerResultVO result = new AnswerResultVO();
        result.setCorrect(isCorrect);
        result.setCorrectAnswer(question.getCorrectAnswer());
        result.setExplanation(question.getExplanation());
        result.setTip(question.getTip());
        
        return result;
    }
}
```

### 3. Mapper 层规范
```java
@Mapper
public interface QuestionMapper {
    
    // 单表查询直接写
    Question selectById(Long id);
    
    // 关联查询使用 resultMap
    Question selectByIdWithOptions(@Param("id") Long id);
    
    // 分页查询
    List<Question> selectPageList(@Param("offset") int offset, 
                                 @Param("limit") int limit,
                                 @Param("categoryId") Integer categoryId);
    
    // 统计查询
    int selectTotalCount(@Param("categoryId") Integer categoryId);
    
    // 插入返回主键
    int insert(Question question);
    
    // 更新非空字段
    int update(Question question);
    
    // 逻辑删除
    int deleteById(@Param("id") Long id);
}
```

### 4. XML 映射文件规范
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.wy.mapper.QuestionMapper">
  
  <!-- 基础 resultMap -->
  <resultMap id="BaseResultMap" type="Question">
    <id property="id" column="id"/>
    <result property="content" column="content"/>
    <result property="imageUrl" column="image_url"/>
    <result property="videoUrl" column="video_url"/>
    <result property="type" column="type"/>
    <result property="tip" column="tip"/>
    <result property="explanation" column="explanation"/>
    <result property="correctAnswer" column="correct_answer"/>
    <result property="difficulty" column="difficulty"/>
    <result property="answerCount" column="answer_count"/>
    <result property="correctCount" column="correct_count"/>
    <result property="status" column="status"/>
    <result property="createdAt" column="created_at"/>
  </resultMap>
  
  <!-- 带选项的 resultMap -->
  <resultMap id="QuestionWithOptionsMap" type="Question" extends="BaseResultMap">
    <collection property="options" ofType="QuestionOption">
      <result property="letter" column="letter"/>
      <result property="content" column="option_content"/>
    </collection>
  </resultMap>
  
  <!-- 公共 SQL 片段 -->
  <sql id="Base_Column_List">
    id, content, image_url, video_url, type, tip, explanation, 
    correct_answer, difficulty, answer_count, correct_count, status, created_at
  </sql>
  
  <sql id="Base_Where">
    WHERE is_deleted = 0 AND status = 1
  </sql>
  
  <!-- 查询方法 -->
  <select id="selectById" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM questions
    WHERE id = #{id}
  </select>
  
  <select id="selectByIdWithOptions" resultMap="QuestionWithOptionsMap">
    SELECT 
      q.*, o.letter, o.content as option_content
    FROM questions q
    LEFT JOIN question_options o ON q.id = o.question_id
    WHERE q.id = #{id} AND q.is_deleted = 0 AND q.status = 1
    ORDER BY o.sort_order
  </select>
  
  <!-- 插入 -->
  <insert id="insert" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO questions 
      (content, image_url, video_url, type, category_id, tip, explanation, 
       correct_answer, difficulty, status, created_at)
    VALUES 
      (#{content}, #{imageUrl}, #{videoUrl}, #{type}, #{categoryId}, #{tip}, #{explanation},
       #{correctAnswer}, #{difficulty}, #{status}, NOW())
  </insert>
  
  <!-- 更新：只更新非空字段 -->
  <update id="update">
    UPDATE questions
    <set>
      <if test="content != null">content = #{content},</if>
      <if test="imageUrl != null">image_url = #{imageUrl},</if>
      <if test="videoUrl != null">video_url = #{videoUrl},</if>
      <if test="tip != null">tip = #{tip},</if>
      <if test="explanation != null">explanation = #{explanation},</if>
      updated_at = NOW()
    </set>
    WHERE id = #{id}
  </update>
  
  <!-- 逻辑删除 -->
  <update id="deleteById">
    UPDATE questions 
    SET is_deleted = 1, updated_at = NOW()
    WHERE id = #{id}
  </update>
  
</mapper>
```

### 5. Entity 规范
```java
@Data
public class Question {
    private Long id;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private Integer type;
    private Integer categoryId;
    private String tip;
    private String explanation;
    private Integer correctAnswer;
    private Integer difficulty;
    private Integer answerCount;
    private Integer correctCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
    
    // 非数据库字段
    @TableField(exist = false)
    private List<QuestionOption> options;
}
```

### 6. DTO 规范
```java
@Data
public class LoginRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度8-20位")
    private String password;
}

@Data
public class PageResponse<T> {
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer size;
    private Boolean hasMore;
}
```

## 异常处理规范

```java
// 统一异常类
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

// 全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("[业务异常] code={}, message={}", e.getErrorCode().getCode(), e.getMessage());
        return Result.error(e.getErrorCode());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[系统异常] ", e);
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }
}
```

## 日志记录规范

```java
// 关键操作日志
log.info("[LOGIN] userId={}, phone={}, ip={}", userId, phone, ip);
log.info("[QUIZ_ANSWER] userId={}, questionId={}, isCorrect={}", userId, questionId, isCorrect);
log.info("[COURSE_PURCHASE] userId={}, courseId={}, amount={}", userId, courseId, amount);

// 业务异常日志
log.warn("[业务警告] code={}, message={}, userId={}", code, message, userId);

// 系统异常日志
log.error("[系统异常] uri={}, params={}, error={}", uri, params, e.getMessage(), e);
```

## 代码注释规范

```java
/**
 * 题目服务实现类
 * 
 * @author xxx
 * @since 1.0.0
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    
    /**
     * 提交答题结果
     * 
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param selectedOption 选择的选项
     * @return 答题结果
     * @throws BusinessException 题目不存在或已删除
     */
    @Override
    public AnswerResultVO submitAnswer(Long userId, Long questionId, Integer selectedOption) {
        // 实现代码
    }
}
```

## Git 提交规范

```
feat: 新增用户登录功能
fix: 修复答题记录统计错误
docs: 更新API文档
refactor: 重构题目查询逻辑
test: 添加单元测试
chore: 更新依赖版本
```

## 检查清单

- [ ] 类名和方法名符合命名规范
- [ ] 所有接口都有 Swagger 注解
- [ ] 数据库操作使用参数化查询（#{}）
- [ ] Service 层有事务注解
- [ ] 关键操作有日志记录
- [ ] 异常有统一处理
- [ ] DTO 有参数校验注解
- [ ] 敏感数据（密码）有加密处理
