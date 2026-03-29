package org.example.wyspring.service;

import org.example.wyspring.dto.request.SlideVerifyRequest;

/**
 * 滑动安全验证（演示实现）。生产可改为极验 / 腾讯云验证码等。
 */
public interface SlideCaptchaService {

	String startSession();

	/**
	 * 校验滑动行为并签发一次性 gateToken，用于后续发短信。
	 */
	String verifyAndIssueGateToken(SlideVerifyRequest request);
}
