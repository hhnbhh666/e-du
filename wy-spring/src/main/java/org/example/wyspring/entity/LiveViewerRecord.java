package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播记录实体（用户观看记录）
 */
@Data
@TableName("live_viewer_records")
public class LiveViewerRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 直播间ID
     */
    private Long roomId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 进入时间
     */
    private LocalDateTime enterTime;

    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;

    /**
     * 观看时长（秒）
     */
    private Integer watchDuration;

    /**
     * 是否点赞
     */
    private Integer liked;

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