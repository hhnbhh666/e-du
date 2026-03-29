package org.example.wyspring.service;

import org.example.wyspring.dto.request.SmsSendRequest;

/**
 * 短信验证码发送与短信登录（含自动注册）。
 */
public interface SmsAuthService {

	void sendLoginCode(SmsSendRequest request);

	String loginBySms(String phone, String code);
}
