package com.campusgo.service;

import com.campusgo.dto.TokenPairResponse;
import com.campusgo.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redis;
    private final JwtService jwtService;

    // access+refresh
    private static final int ACCESS_MINUTES = 15;
    private static final int REFRESH_MINUTES = 7 * 24 * 60;

    private String rtKey(Long userId) {
        return "campusgo:auth:rt:" + userId;
    }


    public TokenPairResponse issueTokens(Long userId) {
        String accessToken = jwtService.issueAccessToken(userId, ACCESS_MINUTES);
        String refreshToken = jwtService.issueRefreshToken(userId, REFRESH_MINUTES);

        long now = Instant.now().getEpochSecond();
        long accessExpiresAt = now + ACCESS_MINUTES * 60L;
        long refreshExpiresAt = now + REFRESH_MINUTES * 60L;

        redis.opsForValue().set(rtKey(userId), refreshToken, REFRESH_MINUTES, TimeUnit.MINUTES);

        return new TokenPairResponse(accessToken, accessExpiresAt, refreshToken, refreshExpiresAt);
    }

    /** 用 refreshToken 换新 accessToken（并可选择滚动 refreshToken） */
    public TokenPairResponse refresh(String refreshToken) {
        try {
            // 1) JWT 层面校验（签名/issuer/exp）
            Jws<Claims> jws = jwtService.parse(refreshToken);

            // 2) 必须是 refresh 类型
            if (!"refresh".equals(jwtService.tokenType(jws))) {
                throw new RuntimeException("Invalid token type");
            }

            // 3) 从 subject 取 userId
            Long userId = Long.valueOf(jws.getBody().getSubject());

            // 4) Redis 白名单校验（必须匹配当前保存的 refreshToken）
            String saved = redis.opsForValue().get(rtKey(userId));
            if (saved == null || !saved.equals(refreshToken)) {
                throw new RuntimeException("Refresh token revoked or not latest");
            }

            // 5) 签发新 token（建议滚动 refresh：发新 refresh 并覆盖旧）
            return issueTokens(userId);
        }
     catch (ExpiredJwtException e) {
        throw new UnauthorizedException("REFRESH_EXPIRED");
    } catch (JwtException e) {
        throw new UnauthorizedException("INVALID_REFRESH_TOKEN");
    }
    }

    /** 登出：删除 refresh 白名单（让 refresh 立刻失效） */
    public void logout(Long userId) {
        redis.delete(rtKey(userId));
    }
}