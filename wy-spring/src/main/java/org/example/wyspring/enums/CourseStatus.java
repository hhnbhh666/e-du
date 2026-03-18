package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 课程状态枚举
 */
@Getter
public enum CourseStatus {

    OFFLINE(0, "下架"),
    ONLINE(1, "上架");

    private final int code;
    private final String desc;

    CourseStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CourseStatus of(int code) {
        for (CourseStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
