package org.example.wyspring.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播间视图对象
 */
@Data
public class LiveRoomVO {

    private Long id;

    private Long anchorId;

    private String anchorName;

    private String anchorAvatar;

    private String anchorTitle;

    private String title;

    private String coverImage;

    private String description;

    private String pushUrl;

    private String pullUrl;

    private Integer status;

    private String statusName;

    private Integer viewerCount;

    private Long totalViewerCount;

    private Long likeCount;

    private LocalDateTime startTime;

    private Integer productCount;

    private Integer categoryId;

    private String categoryName;

    private LocalDateTime createdAt;
}