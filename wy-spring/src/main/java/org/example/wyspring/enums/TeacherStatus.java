package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 教师审核状态枚举
 */
@Getter
public enum TeacherStatus {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝");

    private final int code;
    private final String desc;

    TeacherStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TeacherStatus of(int code) {
        for (TeacherStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
