package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 弹幕类型枚举
 */
@Getter
public enum DanmakuType {

    NORMAL(1, "普通弹幕"),
    GIFT(2, "礼物弹幕"),
    SYSTEM(3, "系统消息");

    private final int code;
    private final String name;

    DanmakuType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}