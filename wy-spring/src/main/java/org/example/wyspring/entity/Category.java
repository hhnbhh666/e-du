package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体
 */
@Data
@TableName("categories")
public class Category {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图标代码/类名
     */
    private String icon;

    /**
     * 背景色
     */
    private String bgColor;

    /**
     * 排序，越小越靠前
     */
    private Integer sortOrder;

    /**
     * 类型：1课程分类 2题目分类
     */
    private Integer type;

    /**
     * 父分类ID，0为顶级
     */
    private Integer parentId;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
