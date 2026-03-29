package org.example.wyspring.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.wyspring.dto.response.Result;
import org.example.wyspring.enums.ErrorCode;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("[业务异常] code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("[参数校验失败] {}", message);
        return Result.error(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("[参数绑定失败] {}", message);
        return Result.error(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理参数校验异常（@RequestParam, @PathVariable）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("[参数校验失败] {}", message);
        return Result.error(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * Redis 不可用（未启动、端口错、密码与 redis.conf 不一致等）
     */
    @ExceptionHandler({RedisConnectionFailureException.class, RedisSystemException.class})
    public Result<Void> handleRedis(Exception e) {
        log.error("[Redis 异常] ", e);
        return Result.error(ErrorCode.REDIS_ERROR,
                "Redis 不可用：请确认本机已启动 Redis，且 application.yml 中 spring.redis.password 与 Redis 配置一致（无密码可留空）");
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[系统异常] ", e);
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }
}
