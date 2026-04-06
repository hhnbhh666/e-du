package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 直播错误码枚举
 */
@Getter
public enum LiveErrorCode implements ErrorCodeInterface {

    // 直播错误 10xxx
    LIVE_ROOM_NOT_FOUND(10001, "直播间不存在"),
    LIVE_ROOM_NOT_LIVING(10002, "直播间未在直播"),
    LIVE_ROOM_ALREADY_LIVING(10003, "该用户已在直播中"),
    LIVE_NOT_PERMISSION(10004, "无权操作该直播间"),
    LIVE_ROOM_BANNED(10005, "直播间已被封禁"),
    LIVE_PRODUCT_NOT_FOUND(10006, "商品不存在"),
    LIVE_PRODUCT_SOLD_OUT(10007, "商品已售罄"),
    LIVE_PRODUCT_OFFLINE(10008, "商品已下架"),
    LIVE_DANMAKU_TOO_FAST(10009, "弹幕发送过于频繁"),
    LIVE_DANMAKU_CONTENT_INVALID(10010, "弹幕内容包含敏感词"),
    LIVE_ANCHOR_NOT_FOUND(10011, "主播不存在"),
    LIVE_VIEWER_NOT_FOUND(10012, "观众记录不存在");

    private final int code;
    private final String message;

    LiveErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}