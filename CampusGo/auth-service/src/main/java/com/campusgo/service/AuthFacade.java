package com.campusgo.service;

import com.campusgo.client.AdminClient;
import com.campusgo.client.UserClient;
import com.campusgo.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AdminClient adminClient;

    private final UserClient userClient;
    private final JwtService jwt;
    private final RefreshTokenStore refreshStore;

    private final int accessMinutes  = 60;          // 1 hour
    private final int refreshMinutes = 60 * 24 * 7; // 7 days



    // for user
    public TokenPairResponse login(LoginRequest req) {
        UserAuthDTO u = userClient.findByUsername(req.getUsername());
        if (u == null || Boolean.FALSE.equals(u.getEnabled())) {
            throw new RuntimeException("USER_NOT_FOUND_OR_DISABLED");
        }
        if (!BCrypt.checkpw(req.getPassword(), u.getPasswordHash())) {
            throw new RuntimeException("BAD_CREDENTIALS");
        }
        return issuePair(u.getId());
    }


    public TokenPairResponse refresh(String refreshToken) {
        Long uid = refreshStore.verifyAndGetUser(refreshToken);
        if (uid == null) throw new RuntimeException("INVALID_REFRESH_TOKEN");

        refreshStore.revoke(refreshToken);
        return issuePair(uid);
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

    private TokenPairResponse issuePair(Long userId) {
        String access  = jwt.issueAccessToken(userId, accessMinutes);
        String refresh = jwt.issueRefreshToken(userId, refreshMinutes);

        long accessExp  = jwt.parse(access).getBody().getExpiration().toInstant().getEpochSecond();
        long refreshExp = jwt.parse(refresh).getBody().getExpiration().toInstant().getEpochSecond();

        refreshStore.save(refresh, userId, refreshExp);
        return new TokenPairResponse(access, accessExp, refresh, refreshExp);
    }

    // for runner

    // for merchant


    // for admin
    public TokenPairResponse loginAdmin(LoginRequest req) {
        AdminAuthDTO admin = adminClient.findByUsername(req.getUsername());
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        if (!BCrypt.checkpw(req.getPassword(), admin.getPasswordHash())) {
            throw new RuntimeException("Bad credentials");
        }

        int accessMinutes  = 60;             // access validation：60 min
        int refreshMinutes = 7 * 24 * 60;    // refresh validation：7 days

        long nowEpoch = Instant.now().getEpochSecond();
        long accessExp  = nowEpoch + accessMinutes  * 60L;
        long refreshExp = nowEpoch + refreshMinutes * 60L;

        String accessToken  = jwt.issueAccessToken(admin.getId(), accessMinutes);
        String refreshToken = jwt.issueRefreshToken(admin.getId(), refreshMinutes);


        refreshStore.save(refreshToken, admin.getId(), refreshExp);


        return new TokenPairResponse(
                accessToken,
                accessExp,
                refreshToken,
                refreshExp
        );
    }
}
