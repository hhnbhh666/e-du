package org.example.wyspring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.wyspring.config.AppSmsProperties;
import org.example.wyspring.dto.request.SmsSendRequest;
import org.example.wyspring.entity.User;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.enums.UserStatus;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.mapper.UserMapper;
import org.example.wyspring.service.SmsAuthService;
import org.example.wyspring.utils.BCryptUtils;
import org.example.wyspring.utils.JwtUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsAuthServiceImpl implements SmsAuthService {

	private static final String SMS_CODE_KEY = "wy:sms:code:";
	private static final String SMS_LOCK_KEY = "wy:sms:lock:";
	private static final String SMS_GATE_KEY = "wy:sms:gate:";
	private static final String SMS_DAY_KEY = "wy:sms:day:";

	private final StringRedisTemplate redis;
	private final AppSmsProperties smsProperties;
	private final UserMapper userMapper;
	private final BCryptUtils bCryptUtils;
	private final JwtUtils jwtUtils;

	@Override
	public void sendLoginCode(SmsSendRequest request) {
		String phone = request.getPhone();
		String gateKey = SMS_GATE_KEY + request.getGateToken();
		Boolean gateOk = redis.hasKey(gateKey);
		if (!Boolean.TRUE.equals(gateOk)) {
			throw new BusinessException(ErrorCode.SMS_GATE_INVALID);
		}
		redis.delete(gateKey);

		String lockKey = SMS_LOCK_KEY + phone;
		Boolean locked = redis.opsForValue().setIfAbsent(lockKey, "1", smsProperties.getResendIntervalSeconds(), TimeUnit.SECONDS);
		if (!Boolean.TRUE.equals(locked)) {
			throw new BusinessException(ErrorCode.SMS_SEND_TOO_FAST);
		}

		String dayKey = SMS_DAY_KEY + phone + ":" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		Long dayCount = redis.opsForValue().increment(dayKey);
		if (dayCount != null && dayCount == 1L) {
			redis.expire(dayKey, 1, TimeUnit.DAYS);
		}
		if (dayCount != null && dayCount > smsProperties.getDailyLimitPerPhone()) {
			throw new BusinessException(ErrorCode.SMS_DAY_LIMIT);
		}

		String code;
		if (StringUtils.isNotBlank(smsProperties.getFixedCode())) {
			code = smsProperties.getFixedCode().trim();
		} else {
			code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
		}
		redis.opsForValue().set(SMS_CODE_KEY + phone, code, smsProperties.getCodeTtlSeconds(), TimeUnit.SECONDS);

		if (smsProperties.isMockEnabled()) {
			log.info("[SMS-MOCK] phone={} code={} (mockEnabled=true, 未调用真实短信网关)", phone, code);
		} else {
			// TODO: 接入阿里云/腾讯云短信
			log.warn("[SMS] mockEnabled=false 但未配置网关实现，请接入后发送 code={} phone={}", code, phone);
		}
	}

	@Override
	@Transactional
	public String loginBySms(String phone, String code) {
		String key = SMS_CODE_KEY + phone;
		String cached = redis.opsForValue().get(key);
		if (cached == null || !cached.equals(code.trim())) {
			throw new BusinessException(ErrorCode.SMS_CODE_INVALID);
		}
		redis.delete(key);

		User user = userMapper.selectByPhone(phone);
		if (user == null) {
			user = new User();
			user.setPhone(phone);
			user.setPassword(bCryptUtils.encode(UUID.randomUUID().toString() + UUID.randomUUID()));
			user.setNickname("用户" + phone.substring(7));
			user.setStatus(UserStatus.ENABLED.getCode());
			userMapper.insert(user);
			log.info("[SMS-REGISTER] userId={}, phone={}", user.getId(), phone);
		} else {
			if (user.getStatus() != null && user.getStatus() == UserStatus.DISABLED.getCode()) {
				throw new BusinessException(ErrorCode.USER_FORBIDDEN);
			}
		}

		String token = jwtUtils.generateToken(user.getId(), user.getPhone());
		log.info("[SMS-LOGIN] userId={}, phone={}", user.getId(), phone);
		return token;
	}
}
