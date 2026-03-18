package org.example.wyspring.enums;

import lombok.Getter;

/**
 * OCR识别状态枚举
 */
@Getter
public enum OcrStatus {

    PROCESSING(0, "处理中"),
    SUCCESS(1, "成功"),
    FAILED(2, "失败");

    private final int code;
    private final String desc;

    OcrStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OcrStatus of(int code) {
        for (OcrStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
