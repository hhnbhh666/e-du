package org.example.wyspring.config;

import lombok.RequiredArgsConstructor;
import org.example.wyspring.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置 - 开发环境禁用JWT验证
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 仅解析 JWT 写入 request，不强制登录；无 token 的接口照常访问
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/slide/start",
                        "/api/auth/slide/verify",
                        "/api/auth/sms/send",
                        "/api/auth/sms/login",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
    }
}
