package org.example.wyspring.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 弹幕视图对象
 */
@Data
public class DanmakuVO {

    private Long id;

    private Long roomId;

    private Long userId;

    private String nickname;

    private String avatar;

    private String content;

    private String color;

    private Integer type;

    private LocalDateTime createdAt;
}