package org.example.wyspring.service;

import org.example.wyspring.dto.request.LoginRequest;
import org.example.wyspring.dto.request.RegisterRequest;
import org.example.wyspring.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    Long register(RegisterRequest request);

    /**
     * 用户登录
     */
    String login(LoginRequest request);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser(Long userId);
}
