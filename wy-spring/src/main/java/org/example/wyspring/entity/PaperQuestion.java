package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 试卷题目关联实体
 */
@Data
@TableName("paper_questions")
public class PaperQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 该题分值
     */
    private Integer score;

    /**
     * 排序
     */
    private Integer sortOrder;
}
