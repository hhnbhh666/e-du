package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目实体
 */
@Data
@TableName("questions")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目配图
     */
    private String imageUrl;

    /**
     * 讲解视频
     */
    private String videoUrl;

    /**
     * 题型：1单选 2多选 3判断
     */
    private Integer type;

    /**
     * 所属分类
     */
    private Integer categoryId;

    /**
     * 出题老师ID
     */
    private Long teacherId;

    /**
     * 答题技巧/口诀（富文本HTML）
     */
    private String tip;

    /**
     * 详细解析（富文本HTML）
     */
    private String explanation;

    /**
     * 正确答案索引：0=A,1=B...
     */
    private Integer correctAnswer;

    /**
     * 被答次数统计
     */
    private Integer answerCount;

    /**
     * 答对次数统计
     */
    private Integer correctCount;

    /**
     * 难度：1简单 2中等 3困难
     */
    private Integer difficulty;

    /**
     * 状态：0待审核 1已通过 2已拒绝 3已下架
     */
    private Integer status;

    /**
     * 审核拒绝原因
     */
    private String reviewReason;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 软删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 选项列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<QuestionOption> options;
}
