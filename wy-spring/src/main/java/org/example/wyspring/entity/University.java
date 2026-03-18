package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大学实体
 */
@Data
@TableName("universities")
public class University {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 大学名称
     */
    private String name;

    /**
     * Logo图片
     */
    private String logo;

    /**
     * 课程数量
     */
    private Integer courseCount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;
}
