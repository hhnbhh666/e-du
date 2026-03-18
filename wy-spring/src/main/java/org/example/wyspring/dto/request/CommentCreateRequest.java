package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建评论请求
 */
@Data
public class CommentCreateRequest {

    @NotNull(message = "对象类型不能为空")
    private Integer targetType;

    @NotNull(message = "对象ID不能为空")
    private Long targetId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容最多1000字符")
    private String content;

    /**
     * 父评论ID，0为顶级评论
     */
    private Long parentId;
}
