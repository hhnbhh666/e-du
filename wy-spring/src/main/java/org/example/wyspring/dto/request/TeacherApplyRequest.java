package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 申请成为教师 / 重新提交
 */
@Data
public class TeacherApplyRequest {

	@NotBlank(message = "姓名不能为空")
	@Size(max = 50, message = "姓名过长")
	private String name;

	@Size(max = 100, message = "职称过长")
	private String title;

	@Size(max = 2000, message = "简介过长")
	private String introduction;

	/**
	 * 教授科目 JSON 数组字符串，如 ["数学","物理"]
	 */
	private String subjects;

	private String avatar;

	private String credentials;
}
