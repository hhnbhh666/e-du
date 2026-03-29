package org.example.wyspring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信与滑动校验相关配置。
 * 生产环境可接入阿里云/腾讯云短信；滑动校验可替换为极验等第三方。
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.sms")
public class AppSmsProperties {

	/**
	 * true：不调用真实短信网关，验证码打印到日志（本地开发）
	 */
	private boolean mockEnabled = true;

	private int codeTtlSeconds = 300;

	private int resendIntervalSeconds = 60;

	private int dailyLimitPerPhone = 10;

	private int gateTokenTtlSeconds = 120;

	private int slideSessionTtlSeconds = 300;

	/**
	 * 非空时使用固定验证码（不写 Redis 随机数）。仅用于开发联调，上线务必留空。
	 */
	private String fixedCode = "";
}
