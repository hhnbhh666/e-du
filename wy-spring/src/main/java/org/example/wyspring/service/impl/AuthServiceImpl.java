package org.example.wyspring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wyspring.dto.request.LoginRequest;
import org.example.wyspring.dto.request.RegisterRequest;
import org.example.wyspring.entity.Teacher;
import org.example.wyspring.entity.User;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.enums.TeacherStatus;
import org.example.wyspring.enums.UserStatus;
import org.example.wyspring.exception.BusinessException;
import org.example.wyspring.mapper.TeacherMapper;
import org.example.wyspring.mapper.UserMapper;
import org.example.wyspring.service.AuthService;
import org.example.wyspring.utils.BCryptUtils;
import org.example.wyspring.utils.JwtUtils;
import org.example.wyspring.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final BCryptUtils bCryptUtils;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public Long register(RegisterRequest request) {
        // 检查手机号是否已存在
        if (userMapper.countByPhone(request.getPhone()) > 0) {
            throw new BusinessException(ErrorCode.USER_PHONE_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(bCryptUtils.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(UserStatus.ENABLED.getCode());

        userMapper.insert(user);

        log.info("[REGISTER] userId={}, phone={}", user.getId(), request.getPhone());

        return user.getId();
    }

    @Override
    public String login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectByPhone(request.getPhone());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (user.getStatus() == UserStatus.DISABLED.getCode()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        // 验证密码
        if (!bCryptUtils.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 生成JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getPhone());

        log.info("[LOGIN] userId={}, phone={}", user.getId(), request.getPhone());

        return token;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());

        // 查询教师信息
        Teacher teacher = teacherMapper.selectByUserId(userId);
        vo.setIsTeacher(teacher != null);
        if (teacher != null) {
            vo.setTeacherStatus(teacher.getStatus());
        }

        return vo;
    }
}
