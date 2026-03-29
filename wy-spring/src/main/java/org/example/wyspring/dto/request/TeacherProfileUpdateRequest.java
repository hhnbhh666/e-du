package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 教师更新个人资料（待审核/已通过时可改部分字段）
 */
@Data
public class TeacherProfileUpdateRequest {

	@Size(max = 50, message = "姓名过长")
	private String name;

	@Size(max = 100, message = "职称过长")
	private String title;

	@Size(max = 2000, message = "简介过长")
	private String introduction;

	private String subjects;

	private String avatar;

	private String credentials;
}
