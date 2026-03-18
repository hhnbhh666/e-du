package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 试卷状态枚举
 */
@Getter
public enum PaperStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下架");

    private final int code;
    private final String desc;

    PaperStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaperStatus of(int code) {
        for (PaperStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
