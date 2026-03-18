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
        // 开发环境：禁用JWT拦截器，所有接口直接访问
        // 生产环境时取消注释下面的代码启用验证
        /*
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/courses/**",
                        "/api/questions/**",
                        "/api/categories/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
        */
    }
}
