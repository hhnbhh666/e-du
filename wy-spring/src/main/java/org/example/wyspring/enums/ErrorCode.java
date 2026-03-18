package org.example.wyspring.enums;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 通用错误 1xxx
    PARAM_ERROR(1001, "参数错误"),
    PARAM_EMPTY(1002, "参数为空"),
    INVALID_PARAM(1003, "参数格式错误"),

    // 用户错误 2xxx
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    USER_PASSWORD_ERROR(2003, "密码错误"),
    USER_NOT_LOGIN(2004, "用户未登录"),
    USER_TOKEN_EXPIRED(2005, "登录已过期"),
    USER_TOKEN_INVALID(2006, "登录状态无效"),
    USER_FORBIDDEN(2007, "用户已被禁用"),
    USER_PHONE_EXISTS(2008, "手机号已注册"),

    // 教师错误 3xxx
    TEACHER_NOT_FOUND(3001, "教师不存在"),
    TEACHER_NOT_APPROVED(3002, "教师资质未通过审核"),
    TEACHER_ALREADY_APPLIED(3003, "已提交教师认证申请"),

    // 课程错误 4xxx
    COURSE_NOT_FOUND(4001, "课程不存在"),
    COURSE_NOT_AVAILABLE(4002, "课程已下架"),

    // 题目错误 5xxx
    QUESTION_NOT_FOUND(5001, "题目不存在"),
    QUESTION_ALREADY_ANSWERED(5002, "题目已作答"),
    QUESTION_NOT_APPROVED(5003, "题目未通过审核"),

    // 评论错误 6xxx
    COMMENT_NOT_FOUND(6001, "评论不存在"),
    COMMENT_NOT_ALLOWED(6002, "未答题不能评论"),

    // 文件上传错误 7xxx
    UPLOAD_ERROR(7001, "文件上传失败"),
    FILE_TOO_LARGE(7002, "文件过大"),
    FILE_TYPE_NOT_SUPPORT(7003, "不支持的文件类型"),

    // OCR错误 8xxx
    OCR_PROCESSING_ERROR(8001, "OCR识别失败"),

    // 系统错误 9xxx
    SYSTEM_ERROR(9001, "系统内部错误"),
    DATABASE_ERROR(9002, "数据库操作失败"),
    REDIS_ERROR(9003, "缓存操作失败"),
    NETWORK_ERROR(9004, "网络请求失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
