package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体
 */
@Data
@TableName("exam_papers")
public class ExamPaper {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷标题
     */
    private String title;

    /**
     * 创建老师ID
     */
    private Long teacherId;

    /**
     * 所属分类
     */
    private Integer categoryId;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 总题数
     */
    private Integer totalQuestions;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 时间限制（分钟），0为不限
     */
    private Integer timeLimit;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 导出的Word文档URL
     */
    private String wordUrl;

    /**
     * 导出的PDF文档URL
     */
    private String pdfUrl;

    /**
     * 状态：0草稿 1已发布 2已下架
     */
    private Integer status;

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
}
