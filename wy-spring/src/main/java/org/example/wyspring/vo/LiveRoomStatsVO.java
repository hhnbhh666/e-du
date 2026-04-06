package org.example.wyspring.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播间统计数据VO
 */
@Data
public class LiveRoomStatsVO {

    private Long roomId;

    private Integer currentViewers;

    private Long totalViewers;

    private Long totalWatchDuration;

    private Long likeCount;

    private Long danmakuCount;

    private Integer productCount;

    private Long totalSales;
}