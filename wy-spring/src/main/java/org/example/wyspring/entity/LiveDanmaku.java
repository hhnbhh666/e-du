package org.example.wyspring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播弹幕实体
 */
@Data
@TableName("live_danmaku")
public class LiveDanmaku {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 直播间ID
     */
    private Long roomId;

    /**
     * 发送者用户ID
     */
    private Long userId;

    /**
     * 发送者昵称
     */
    private String nickname;

    /**
     * 发送者头像
     */
    private String avatar;

    /**
     * 弹幕内容
     */
    private String content;

    /**
     * 弹幕颜色（十六进制，如 #FFFFFF）
     */
    private String color;

    /**
     * 弹幕类型：1普通 2礼物弹幕 3系统消息
     */
    private Integer type;

    /**
     * 发送时间
     */
    private LocalDateTime createdAt;

    /**
     * 软删除
     */
    @TableLogic
    private Integer isDeleted;
}