package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播间实体
 */
@Data
@TableName("live_rooms")
public class LiveRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主播用户ID（关联users表）
     */
    private Long anchorId;

    /**
     * 直播间标题
     */
    private String title;

    /**
     * 直播间封面图
     */
    private String coverImage;

    /**
     * 直播间描述
     */
    private String description;

    /**
     * 推流地址
     */
    private String pushUrl;

    /**
     * 拉流地址（播放地址）
     */
    private String pullUrl;

    /**
     * 状态：0未开播 1直播中 2已下播 3被封禁
     */
    private Integer status;

    /**
     * 当前观看人数（在线人数）
     */
    private Integer viewerCount;

    /**
     * 累计观看人数
     */
    private Long totalViewerCount;

    /**
     * 累计观看时长（秒）
     */
    private Long totalWatchDuration;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 开始直播时间
     */
    private LocalDateTime startTime;

    /**
     * 结束直播时间
     */
    private LocalDateTime endTime;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 商品数量
     */
    private Integer productCount;

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