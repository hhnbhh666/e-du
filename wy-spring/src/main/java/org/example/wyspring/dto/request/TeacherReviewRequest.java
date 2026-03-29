package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理员审核教师
 */
@Data
public class TeacherReviewRequest {

	/**
	 * true 通过，false 拒绝
	 */
	@NotNull(message = "approve 不能为空")
	private Boolean approve;

	/**
	 * 拒绝时建议填写
	 */
	private String reviewReason;
}
