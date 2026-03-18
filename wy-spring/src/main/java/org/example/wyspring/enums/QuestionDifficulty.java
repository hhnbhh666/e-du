package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 题目难度枚举
 */
@Getter
public enum QuestionDifficulty {

    EASY(1, "简单"),
    MEDIUM(2, "中等"),
    HARD(3, "困难");

    private final int code;
    private final String desc;

    QuestionDifficulty(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static QuestionDifficulty of(int code) {
        for (QuestionDifficulty difficulty : values()) {
            if (difficulty.code == code) {
                return difficulty;
            }
        }
        return null;
    }
}
