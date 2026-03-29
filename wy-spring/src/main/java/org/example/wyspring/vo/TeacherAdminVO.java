package org.example.wyspring.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台教师列表/详情
 */
@Data
public class TeacherAdminVO {

	private Long id;
	private Long userId;
	private String phone;
	private String userNickname;
	private String name;
	private String avatar;
	private String title;
	private String introduction;
	private String subjects;
	private String credentials;
	private Integer status;
	private String statusText;
	private String reviewReason;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
