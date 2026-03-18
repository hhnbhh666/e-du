package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@TableName("courses")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 副标题/简介
     */
    private String subtitle;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 课程详细介绍
     */
    private String description;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 讲师ID
     */
    private Long teacherId;

    /**
     * 现价
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 总集数
     */
    private Integer episodesCount;

    /**
     * 学员数量
     */
    private Integer studentsCount;

    /**
     * 状态：1上架 0下架
     */
    private Integer status;

    /**
     * 排序权重
     */
    private Integer sortOrder;

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
