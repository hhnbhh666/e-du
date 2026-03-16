

---

## 六、老师认证接口

### 1. 申请成为老师

**URL**: `POST /api/v1/teacher/apply`

**请求参数**:
```json
{
  "name": "张老师",
  "title": "高级讲师",
  "introduction": "10年驾考培训经验...",
  "subjects": ["科目一", "科目四"],
  "credentials": ["https://xxx.com/cert1.jpg", "https://xxx.com/cert2.jpg"]
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| name | string | 是 | 真实姓名 |
| title | string | 是 | 职称/头衔 |
| introduction | string | 是 | 个人简介 |
| subjects | array | 是 | 教授科目列表 |
| credentials | array | 否 | 资质证书图片URL列表 |

**响应**:
```json
{
  "code": 0,
  "message": "申请已提交，等待审核",
  "data": {
    "teacherId": 1001,
    "status": 0,
    "statusText": "待审核"
  }
}
```

### 2. 获取老师认证状态

**URL**: `GET /api/v1/teacher/status`

**响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "teacherId": 1001,
    "name": "张老师",
    "title": "高级讲师",
    "status": 1,
    "statusText": "已通过",
    "reviewReason": null
  }
}
```

---

## 七、老师题目管理接口（需老师权限）

### 1. 创建题目

**URL**: `POST /api/v1/teacher/questions`

**请求参数**:
```json
{
  "content": "遇到图中情形可加速通过。",
  "image": "https://xxx.com/question.jpg",
  "videoUrl": "https://xxx.com/explain.mp4",
  "type": 1,
  "categoryId": 3,
  "difficulty": 2,
  "options": [
    { "letter": "A", "text": "正确" },
    { "letter": "B", "text": "错误" }
  ],
  "correctAnswer": 1,
  "tip": "遇路口，先减速。",
  "explanation": "<p>图中显示的是<strong>让行标志</strong>，遇到这种情形应当减速慢行...</p><img src='https://xxx.com/demo.jpg'/>"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| content | string | 是 | 题目内容（纯文本） |
| image | string | 否 | 题目配图URL |
| videoUrl | string | 否 | 讲解视频URL |
| type | integer | 是 | 题型：1单选 2多选 3判断 |
| categoryId | integer | 是 | 分类ID |
| difficulty | integer | 是 | 难度：1简单 2中等 3困难 |
| options | array | 是 | 选项列表，每个包含letter和text |
| correctAnswer | integer | 是 | 正确答案索引：0=A,1=B... |
| tip | string | 否 | 答题技巧（支持HTML富文本） |
| explanation | string | 否 | 详细解析（支持HTML富文本，可包含图片） |

**响应**:
```json
{
  "code": 0,
  "message": "题目创建成功，等待审核",
  "data": {
    "questionId": 10086,
    "status": 0,
    "statusText": "待审核"
  }
}
```

**说明**: 老师创建的题目默认状态为"待审核"，需管理员审核通过后才能上架。

### 2. 更新题目

**URL**: `PUT /api/v1/teacher/questions/{id}`

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 题目ID |

**请求参数**: 同创建题目

**响应**: 
```json
{
  "code": 0,
  "message": "题目更新成功，等待审核",
  "data": {
    "questionId": 10086,
    "status": 0,
    "statusText": "待审核"
  }
}
```

**说明**: 修改后题目会重新进入审核状态。

### 3. 获取老师创建的题目列表

**URL**: `GET /api/v1/teacher/questions`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | integer | 否 | 页码，默认1 |
| size | integer | 否 | 每页数量，默认20 |
| status | integer | 否 | 状态筛选：0待审核 1已通过 2已拒绝 |
| keyword | string | 否 | 关键词搜索 |

**响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 10086,
        "content": "遇到图中情形可加速通过。",
        "type": 1,
        "difficulty": 2,
        "status": 1,
        "statusText": "已通过",
        "answerCount": 156,
        "correctCount": 89,
        "createdAt": "2024-01-15T10:30:00"
      }
    ],
    "total": 28,
    "page": 1,
    "hasMore": true
  }
}
```

### 4. 删除题目

**URL**: `DELETE /api/v1/teacher/questions/{id}`

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 题目ID |

**响应**:
```json
{
  "code": 0,
  "message": "题目已删除",
  "data": null
}
```

---

## 八、OCR识别接口（需老师权限）

### 1. 上传试卷图片进行OCR识别

**URL**: `POST /api/v1/ocr/recognize`

**请求方式**: `multipart/form-data`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| file | file | 是 | 试卷图片文件（jpg/png，最大10MB） |
| categoryId | integer | 是 | 识别后的题目所属分类 |

**响应**:
```json
{
  "code": 0,
  "message": "识别完成",
  "data": {
    "recordId": 5001,
    "status": 1,
    "originalImage": "https://xxx.com/upload/original.jpg",
    "parsedQuestions": [
      {
        "content": "遇到图中情形可加速通过。",
        "options": [
          { "letter": "A", "text": "正确" },
          { "letter": "B", "text": "错误" }
        ],
        "correctAnswer": 1,
        "confidence": 0.95
      }
    ],
    "totalCount": 5,
    "successCount": 4,
    "failCount": 1
  }
}
```

**说明**: 
- 使用百度OCR API进行文字识别
- 识别结果需要老师确认后批量导入
- confidence表示识别置信度，低于0.8的建议人工核对

### 2. 获取OCR识别记录

**URL**: `GET /api/v1/ocr/records`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | integer | 否 | 页码，默认1 |
| size | integer | 否 | 每页数量，默认10 |

**响应**: 包含历史识别记录列表

### 3. 确认并导入OCR识别的题目

**URL**: `POST /api/v1/ocr/records/{id}/import`

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | OCR记录ID |

**请求参数**:
```json
{
  "questions": [
    {
      "content": "遇到图中情形可加速通过。",
      "type": 1,
      "categoryId": 3,
      "difficulty": 2,
      "options": [
        { "letter": "A", "text": "正确" },
        { "letter": "B", "text": "错误" }
      ],
      "correctAnswer": 1,
      "tip": "遇路口，先减速。",
      "explanation": "图中显示让行标志..."
    }
  ],
  "createPaper": true,
  "paperTitle": "2024年科目一模拟试卷"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| questions | array | 是 | 确认后的题目列表（可修改OCR识别结果） |
| createPaper | boolean | 否 | 是否同时创建试卷，默认false |
| paperTitle | string | 条件 | 创建试卷时的标题（createPaper=true时必填） |

**响应**:
```json
{
  "code": 0,
  "message": "成功导入5道题目，并创建试卷",
  "data": {
    "importedCount": 5,
    "paperId": 1001,
    "paperTitle": "2024年科目一模拟试卷"
  }
}
```

---

## 九、试卷管理接口（需老师权限）

### 1. 创建试卷

**URL**: `POST /api/v1/teacher/papers`

**请求参数**:
```json
{
  "title": "2024年科目一模拟试卷",
  "categoryId": 3,
  "description": "本试卷包含100道科目一理论题...",
  "totalScore": 100,
  "timeLimit": 45,
  "coverImage": "https://xxx.com/paper-cover.jpg"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| title | string | 是 | 试卷标题 |
| categoryId | integer | 是 | 试卷分类 |
| description | string | 否 | 试卷描述 |
| totalScore | integer | 否 | 总分，默认100 |
| timeLimit | integer | 否 | 时间限制（分钟），0为不限时 |
| coverImage | string | 否 | 封面图片 |

**响应**:
```json
{
  "code": 0,
  "message": "试卷创建成功",
  "data": {
    "paperId": 1001,
    "title": "2024年科目一模拟试卷",
    "status": 0
  }
}
```

### 2. 添加题目到试卷

**URL**: `POST /api/v1/teacher/papers/{id}/questions`

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 试卷ID |

**请求参数**:
```json
{
  "questions": [
    {
      "questionId": 10086,
      "score": 1,
      "sortOrder": 1
    },
    {
      "questionId": 10087,
      "score": 1,
      "sortOrder": 2
    }
  ]
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| questions | array | 是 | 题目列表 |
| questionId | long | 是 | 题目ID |
| score | integer | 否 | 该题分值，默认1 |
| sortOrder | integer | 否 | 排序，默认按添加顺序 |

### 3. 从试卷移除题目

**URL**: `DELETE /api/v1/teacher/papers/{paperId}/questions/{questionId}`

### 4. 获取试卷列表

**URL**: `GET /api/v1/teacher/papers`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | integer | 否 | 页码，默认1 |
| size | integer | 否 | 每页数量，默认20 |
| status | integer | 否 | 状态筛选：0草稿 1已发布 2已下架 |

**响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1001,
        "title": "2024年科目一模拟试卷",
        "categoryId": 3,
        "totalQuestions": 100,
        "totalScore": 100,
        "timeLimit": 45,
        "status": 1,
        "createdAt": "2024-01-15T10:30:00"
      }
    ],
    "total": 5
  }
}
```

### 5. 获取试卷详情（含题目）

**URL**: `GET /api/v1/teacher/papers/{id}`

**响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1001,
    "title": "2024年科目一模拟试卷",
    "description": "本试卷包含100道科目一理论题...",
    "totalQuestions": 100,
    "totalScore": 100,
    "timeLimit": 45,
    "coverImage": "https://xxx.com/cover.jpg",
    "status": 1,
    "questions": [
      {
        "id": 10086,
        "content": "遇到图中情形可加速通过。",
        "type": 1,
        "score": 1,
        "sortOrder": 1,
        "options": [...]
      }
    ]
  }
}
```

### 6. 导出试卷为Word文档

**URL**: `POST /api/v1/teacher/papers/{id}/export/word`

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 试卷ID |

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| includeAnswer | boolean | 否 | 是否包含答案，默认true |
| includeExplanation | boolean | 否 | 是否包含解析，默认true |

**响应**:
```json
{
  "code": 0,
  "message": "导出成功",
  "data": {
    "downloadUrl": "https://xxx.com/exports/paper_1001_20240115.docx",
    "fileName": "2024年科目一模拟试卷.docx",
    "expireTime": "2024-01-15T12:30:00"
  }
}
```

**说明**:
- 使用Apache POI生成Word文档
- 富文本解析会自动转换为Word格式
- 包含题目图片（如果有）
- 下载链接有效期2小时

### 7. 发布/下架试卷

**URL**: `PUT /api/v1/teacher/papers/{id}/status`

**请求参数**:
```json
{
  "status": 1
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| status | integer | 是 | 1发布 2下架 |

---

## 十、文件上传接口

### 1. 上传图片

**URL**: `POST /api/v1/upload/image`

**请求方式**: `multipart/form-data`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| file | file | 是 | 图片文件（jpg/png/gif，最大5MB） |
| folder | string | 否 | 存储文件夹：questions/avatars/papers，默认others |

**响应**:
```json
{
  "code": 0,
  "message": "上传成功",
  "data": {
    "url": "https://cdn.xxx.com/images/2024/01/xxx.jpg",
    "width": 800,
    "height": 600,
    "size": 123456
  }
}
```

### 2. 上传视频

**URL**: `POST /api/v1/upload/video`

**请求方式**: `multipart/form-data`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| file | file | 是 | 视频文件（mp4，最大100MB） |

**响应**: 同图片上传，包含视频URL和时长

---

## 附录：百度OCR配置

### 配置文件 (application.yml)

```yaml
ocr:
  baidu:
    app-id: your_app_id
    api-key: w48BCeNrGe9IzId0bIcB9HNa
    secret-key: oRUfXJV5YgW9WoiYtS8RyDHt3Ze3JUho
    connection-timeout: 5000
    socket-timeout: 10000
```

### 使用说明

1. **文字识别**: 调用百度通用文字识别API（高精度版）
2. **题目结构化**: 后端使用正则表达式或NLP提取题目内容、选项、答案
3. **置信度评估**: 根据OCR返回的probability字段评估识别质量
4. **图片预处理**: 上传前自动压缩、裁剪，提高识别准确率
