package org.example.wyspring.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.wyspring.config.AppSmsProperties;
import org.example.wyspring.dto.request.SlideVerifyRequest;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.service.SlideCaptchaService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SlideCaptchaServiceImpl implements SlideCaptchaService {

	private static final String SLIDE_KEY = "wy:slide:";

	private final StringRedisTemplate redis;
	private final AppSmsProperties smsProperties;

	@Override
	public String startSession() {
		String slideId = UUID.randomUUID().toString().replace("-", "");
		long now = System.currentTimeMillis();
		String key = SLIDE_KEY + slideId;
		redis.opsForValue().set(key, String.valueOf(now), smsProperties.getSlideSessionTtlSeconds(), TimeUnit.SECONDS);
		return slideId;
	}

	@Override
	public String verifyAndIssueGateToken(SlideVerifyRequest request) {
		String key = SLIDE_KEY + request.getSlideId();
		String startStr = redis.opsForValue().get(key);
		if (startStr == null) {
			throw new BusinessException(ErrorCode.SLIDE_CAPTCHA_EXPIRED);
		}
		long start = Long.parseLong(startStr);
		long serverElapsed = System.currentTimeMillis() - start;
		if (serverElapsed < 500L) {
			throw new BusinessException(ErrorCode.SLIDE_CAPTCHA_INVALID);
		}
		Long durationMs = request.getDurationMs();
		if (durationMs == null || durationMs < 400L || durationMs > 120_000L) {
			throw new BusinessException(ErrorCode.SLIDE_CAPTCHA_INVALID);
		}
		Integer progress = request.getProgress();
		if (progress == null || progress < 88) {
			throw new BusinessException(ErrorCode.SLIDE_CAPTCHA_INVALID);
		}

		redis.delete(key);

		String gateToken = UUID.randomUUID().toString().replace("-", "");
		String gateKey = "wy:sms:gate:" + gateToken;
		redis.opsForValue().set(gateKey, "1", smsProperties.getGateTokenTtlSeconds(), TimeUnit.SECONDS);
		return gateToken;
	}
}
