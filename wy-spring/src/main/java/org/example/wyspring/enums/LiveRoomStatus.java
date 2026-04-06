package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 直播间状态枚举
 */
@Getter
public enum LiveRoomStatus {

    NOT_STARTED(0, "未开播"),
    LIVING(1, "直播中"),
    ENDED(2, "已下播"),
    BANNED(3, "被封禁");

    private final int code;
    private final String name;

    LiveRoomStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LiveRoomStatus fromCode(Integer code) {
        if (code == null) {
            return NOT_STARTED;
        }
        for (LiveRoomStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return NOT_STARTED;
    }
}