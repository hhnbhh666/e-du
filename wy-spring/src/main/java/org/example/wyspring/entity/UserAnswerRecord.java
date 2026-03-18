package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户答题记录实体
 */
@Data
@TableName("user_answer_records")
public class UserAnswerRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 选择的选项索引
     */
    private Integer selectedOption;

    /**
     * 是否正确：1正确 0错误
     */
    private Integer isCorrect;

    /**
     * 答题时间
     */
    private LocalDateTime createdAt;
}
