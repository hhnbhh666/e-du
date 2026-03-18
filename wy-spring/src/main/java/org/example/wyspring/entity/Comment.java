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
 * 评论实体
 */
@Data
@TableName("comments")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 对象类型：1题目 2课程
     */
    private Integer targetType;

    /**
     * 对象ID
     */
    private Long targetId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID，0为顶级评论
     */
    private Long parentId;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 是否官方/讲师回复：1是 0否
     */
    private Integer isAuthor;

    /**
     * 状态：1正常 0屏蔽
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

    /**
     * 用户昵称（非数据库字段）
     */
    @TableField(exist = false)
    private String userNickname;

    /**
     * 用户头像（非数据库字段）
     */
    @TableField(exist = false)
    private String userAvatar;

    /**
     * 回复列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Comment> replies;
}
