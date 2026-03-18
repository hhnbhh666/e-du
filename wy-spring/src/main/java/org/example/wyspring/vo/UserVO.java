package org.example.wyspring.vo;

import lombok.Data;

/**
 * 用户视图对象
 */
@Data
public class UserVO {

    private Long id;

    private String phone;

    private String nickname;

    private String avatar;

    private Integer status;

    /**
     * 是否是教师
     */
    private Boolean isTeacher;

    /**
     * 教师审核状态（如果是教师）
     */
    private Integer teacherStatus;
}
