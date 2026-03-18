package org.example.wyspring.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
public class CommentVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private String content;

    private Integer likes;

    private Boolean isAuthor;

    private LocalDateTime createdAt;

    /**
     * 回复列表
     */
    private List<CommentVO> replies;

    /**
     * 回复数量
     */
    private Integer replyCount;
}
