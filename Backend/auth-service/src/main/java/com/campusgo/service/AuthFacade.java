package com.campusgo.service;

import com.campusgo.client.AdminClient;
import com.campusgo.client.MerchantClient;
import com.campusgo.client.RunnerClient;
import com.campusgo.client.UserClient;
import com.campusgo.dto.*;
import com.campusgo.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.codec.digest.DigestUtils.sha256;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AdminClient adminClient;
    private final MerchantClient merchantClient;

    private final RunnerClient runnerClient;

    private final UserClient userClient;

    private final RefreshTokenStore refreshStore;
    private final StringRedisTemplate redis;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthFacade.class);


    private final JwtService jwt;
    private static final long IDEM_TTL_SECONDS = 60;
    private static final long LOCK_TTL_SECONDS = 5;

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end",
            Long.class
    );



    // for user
    public TokenPairResponse login(LoginRequest req) {
        UserAuthDTO u = userClient.findByUsername(req.getUsername());
        if (u == null || Boolean.FALSE.equals(u.getEnabled())) {
            throw new RuntimeException("USER_NOT_FOUND_OR_DISABLED");
        }
        if (!BCrypt.checkpw(req.getPassword(), u.getPasswordHash())) {
            throw new RuntimeException("BAD_CREDENTIALS");
        }
        return issuePairForPrincipal(u.getId(), "USER");
    }


    public TokenPairResponse refresh(String refreshToken) {
        String idemKey = "campusgo:idem:auth:refresh:" + sha256(refreshToken);
        String cached = redis.opsForValue().get(idemKey);

        if (cached != null) {
                try {
                    return objectMapper.readValue(cached, TokenPairResponse.class);
                } catch (Exception e) {
                    redis.delete(idemKey);
                }
        }

        Long uid = refreshStore.verifyAndGetUser(refreshToken);
        if (uid == null) throw new RuntimeException("INVALID_REFRESH_TOKEN");

        String lockKey = "campusgo:lock:auth:refresh:" + uid;
        String lockVal = String.valueOf(Instant.now().toEpochMilli()) + ":" + Thread.currentThread().getId();

        Boolean locked = redis.opsForValue().setIfAbsent(lockKey, lockVal, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            // 有并发 refresh：再读一次幂等结果，读到就返回；读不到就提示重试
            String again = redis.opsForValue().get(idemKey);
            if (again != null) {
                try {
                    return objectMapper.readValue(again, TokenPairResponse.class);
                } catch (Exception e) {
                    redis.delete(idemKey);
                }
            }
            throw new RuntimeException("TOO_MANY_REQUESTS");
        }
        try {
            // 3) 双重检查幂等结果（拿到锁后再检查一次，避免重复签发）
            String again = redis.opsForValue().get(idemKey);
            if (again != null) {
                try {
                    return objectMapper.readValue(again, TokenPairResponse.class);
                } catch (Exception e) {
                    redis.delete(idemKey);
                }
            }

            // 4) 先签发新的一对（rotation）
            TokenPairResponse resp = issuePair(uid);

            // 5) 再 revoke 旧 refreshToken（让旧 token 失效）
            refreshStore.revoke(refreshToken);

            // 6) 写入幂等缓存（重复 refresh 返回同一 resp）
            try {
                redis.opsForValue().set(
                        idemKey,
                        objectMapper.writeValueAsString(resp),
                        IDEM_TTL_SECONDS,
                        TimeUnit.SECONDS
                );
            } catch (Exception e) {
                log.warn("Failed to cache refresh response for idemKey={}", idemKey, e);
            }

            return resp;

        } finally {
            // 7) 安全释放锁：只删除自己加的锁
            redis.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockVal);
        }
    }

    private TokenPairResponse issuePair(Long uid) {
        return tokenService.issueTokens(uid);
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(s.hashCode());
        }
    }

    public void logout(String refreshToken) {
        refreshStore.revoke(refreshToken);
    }

    public UserAuthDTO register(RegisterRequest req) {
        String hash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
        UserClient.UserRegisterReq body = new UserClient.UserRegisterReq();
        body.username = req.getUsername();
        body.passwordHash = hash;
        body.phone = req.getPhone();
        return userClient.register(body);
    }

    // for runner
    public TokenPairResponse loginRunner(LoginRequest req) {
        RunnerAuthDTO runner = runnerClient.findByUsername(req.getUsername());
        if (runner == null || Boolean.FALSE.equals(runner.getEnabled())) {
            throw new RuntimeException("RUNNER_NOT_FOUND_OR_DISABLED");
        }
        if (!BCrypt.checkpw(req.getPassword(), runner.getPasswordHash())) {
            throw new RuntimeException("BAD_CREDENTIALS");
        }
        return issuePairForPrincipal(runner.getId(), "RUNNER");
    }

    // for merchant
    public TokenPairResponse loginMerchant(LoginRequest req) {
        MerchantAuthDTO merchant = merchantClient.findByUsername(req.getUsername());
        if (merchant == null || Boolean.FALSE.equals(merchant.getEnabled())) {
            throw new RuntimeException("MERCHANT_NOT_FOUND_OR_DISABLED");
        }
        if (!BCrypt.checkpw(req.getPassword(), merchant.getPasswordHash())) {
            throw new RuntimeException("BAD_CREDENTIALS");
        }
        return issuePairForPrincipal(merchant.getId(), "MERCHANT");
    }



    // for admin
    public TokenPairResponse loginAdmin(LoginRequest req) {
        AdminAuthDTO admin = adminClient.findByUsername(req.getUsername());
        if (admin == null) {
            throw new RuntimeException("ADMIN_NOT_FOUND");
        }
        if (!BCrypt.checkpw(req.getPassword(), admin.getPasswordHash())) {
            throw new RuntimeException("BAD_CREDENTIALS");
        }
        return issuePairForPrincipal(admin.getId(), "ADMIN");


    }

    //for common use
    private TokenPairResponse issuePairForPrincipal(Long principalId, String principalType) {
        int accessMinutes  = 60;
        int refreshMinutes = 7 * 24 * 60; // 7 days

        long nowEpoch = Instant.now().getEpochSecond();
        long accessExp  = nowEpoch + accessMinutes  * 60L;
        long refreshExp = nowEpoch + refreshMinutes * 60L;

        String accessToken  = jwt.issueAccessToken(principalId, principalType,accessMinutes);
        String refreshToken = jwt.issueRefreshToken(principalId, principalType,refreshMinutes);

        // 统一用 refreshStore 管 refresh（你现在就是 refreshToken -> userId）
        refreshStore.save(refreshToken, principalId, refreshExp);

        return new TokenPairResponse(
                accessToken,
                accessExp,
                refreshToken,
                refreshExp
        );
    }
}
