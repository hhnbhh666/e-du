package org.example.wyspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wyspring.dto.request.LoginRequest;
import org.example.wyspring.dto.request.RegisterRequest;
import org.example.wyspring.dto.request.SlideVerifyRequest;
import org.example.wyspring.dto.request.SmsLoginRequest;
import org.example.wyspring.dto.request.SmsSendRequest;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.service.AuthService;
import org.example.wyspring.service.SlideCaptchaService;
import org.example.wyspring.service.SmsAuthService;
import org.example.wyspring.utils.CurrentUserUtils;
import org.example.wyspring.vo.SlideStartVO;
import org.example.wyspring.vo.SmsGateTokenVO;
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
    private final SlideCaptchaService slideCaptchaService;
    private final SmsAuthService smsAuthService;

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

    @PostMapping("/slide/start")
    @Operation(summary = "开始滑动验证（获取 slideId）")
    public Result<SlideStartVO> slideStart() {
        String slideId = slideCaptchaService.startSession();
        return Result.success(new SlideStartVO(slideId));
    }

    @PostMapping("/slide/verify")
    @Operation(summary = "完成滑动验证，获取发短信用 gateToken")
    public Result<SmsGateTokenVO> slideVerify(@RequestBody @Valid SlideVerifyRequest request) {
        String gateToken = slideCaptchaService.verifyAndIssueGateToken(request);
        return Result.success(new SmsGateTokenVO(gateToken));
    }

    @PostMapping("/sms/send")
    @Operation(summary = "发送登录短信验证码（需 gateToken）")
    public Result<Void> sendSmsCode(@RequestBody @Valid SmsSendRequest request) {
        smsAuthService.sendLoginCode(request);
        return Result.success(null);
    }

    @PostMapping("/sms/login")
    @Operation(summary = "短信验证码登录（新用户自动注册）")
    public Result<Map<String, Object>> smsLogin(@RequestBody @Valid SmsLoginRequest request) {
        String token = smsAuthService.loginBySms(request.getPhone(), request.getCode());
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
