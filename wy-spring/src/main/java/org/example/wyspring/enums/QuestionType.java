package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 题目类型枚举
 */
@Getter
public enum QuestionType {

    SINGLE_CHOICE(1, "单选题"),
    MULTIPLE_CHOICE(2, "多选题"),
    TRUE_FALSE(3, "判断题");

    private final int code;
    private final String desc;

    QuestionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static QuestionType of(int code) {
        for (QuestionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
