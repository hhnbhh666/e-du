package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师实体
 */
@Data
@TableName("teachers")
public class Teacher {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 职称/头衔
     */
    private String title;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 教授科目（JSON数组）
     */
    private String subjects;

    /**
     * 资质证书图片（JSON数组）
     */
    private String credentials;

    /**
     * 状态：0待审核 1通过 2拒绝
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
}
