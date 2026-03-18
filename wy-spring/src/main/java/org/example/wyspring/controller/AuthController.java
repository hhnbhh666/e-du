package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.LoginRequest;
import org.example.wyspring.dto.request.RegisterRequest;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.AuthService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Long> register(@RequestBody @Valid RegisterRequest request) {
        Long userId = authService.register(request);
        return Result.success(userId);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("tokenType", "Bearer");

        return Result.success(result);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> getCurrentUser() {
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(org.example.wyspring.enums.ErrorCode.USER_NOT_LOGIN);
        }
        UserVO user = authService.getCurrentUser(userId);
        return Result.success(user);
    }
}
