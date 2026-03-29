package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SmsLoginRequest {

	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
	private String phone;

	@NotBlank(message = "验证码不能为空")
	@Size(min = 4, max = 8, message = "验证码长度不正确")
	private String code;
}
