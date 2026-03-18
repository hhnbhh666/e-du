package org.example.wyspring.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret:wy-spring-default-secret-key}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_PHONE = "phone";

    /**
     * 生成JWT Token
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @return JWT Token
     */
    public String generateToken(Long userId, String phone) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_PHONE, phone);

        return JWT.create()
                .withClaim(CLAIM_USER_ID, userId)
                .withClaim(CLAIM_PHONE, phone)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 验证并解析Token
     *
     * @param token JWT Token
     * @return DecodedJWT
     */
    public DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.warn("[JWT验证失败] {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        DecodedJWT jwt = verifyToken(token);
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim(CLAIM_USER_ID).asLong();
    }

    /**
     * 从Token中获取手机号
     *
     * @param token JWT Token
     * @return 手机号
     */
    public String getPhone(String token) {
        DecodedJWT jwt = verifyToken(token);
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim(CLAIM_PHONE).asString();
    }

    /**
     * 判断Token是否过期
     *
     * @param token JWT Token
     * @return true-已过期或无效
     */
    public boolean isExpired(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt == null || jwt.getExpiresAt().before(new Date());
    }
}
