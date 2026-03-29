package org.example.wyspring.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 滑动验证完成上报（可替换为极验 validate 结果回传）。
 */
@Data
public class SlideVerifyRequest {

	@NotBlank(message = "slideId 不能为空")
	private String slideId;

	/**
	 * 从按下到松开的耗时（毫秒）
	 */
	@NotNull(message = "durationMs 不能为空")
	@Min(value = 400, message = "durationMs 过短")
	@Max(value = 120000, message = "durationMs 过长")
	private Long durationMs;

	/**
	 * 滑块最终位置比例 0~1，需接近轨道末端
	 */
	@NotNull(message = "progress 不能为空")
	@Min(value = 0, message = "progress 非法")
	@Max(value = 100, message = "progress 非法")
	private Integer progress;
}
