package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 直播商品状态枚举
 */
@Getter
public enum LiveProductStatus {

    OFFLINE(0, "已下架"),
    ONLINE(1, "上架中"),
    SOLD_OUT(2, "已售罄");

    private final int code;
    private final String name;

    LiveProductStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LiveProductStatus fromCode(Integer code) {
        if (code == null) {
            return OFFLINE;
        }
        for (LiveProductStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return OFFLINE;
    }
}