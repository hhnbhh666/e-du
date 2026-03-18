package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 题目选项实体
 */
@Data
@TableName("question_options")
public class QuestionOption {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属题目ID
     */
    private Long questionId;

    /**
     * 选项字母：A/B/C/D
     */
    private String letter;

    /**
     * 选项内容
     */
    private String content;

    /**
     * 排序
     */
    private Integer sortOrder;
}
