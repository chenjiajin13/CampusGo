package com.campusgo.controller;

import com.campusgo.config.JwtProps;
import com.campusgo.dto.TokenResponse;
import com.campusgo.dto.ValidateResponse;
import com.campusgo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class AuthInternalController {

    private final JwtService jwt;
    private final JwtProps props;

    @PostMapping("/token")
    public TokenResponse issue(@RequestParam("userId") Long userId) {
        String token = jwt.issueAccessToken(userId, "user", props.getExpiresMinutes());
        // analyze the expiration time
        Jws<Claims> jws = jwt.parse(token);
        long exp = jws.getBody().getExpiration().toInstant().getEpochSecond();
        return new TokenResponse(token, exp);
    }
    /**
     * check token for(gateway)
     */
    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestParam("token") String token) {
        try {
            Jws<Claims> jws = jwt.parse(token);
            String pt = jwt.principalType(jws);
            Long uid = Long.valueOf(jws.getBody().getSubject());
            long exp = jws.getBody().getExpiration().toInstant().getEpochSecond();
            return ResponseEntity.ok(new ValidateResponse(true, uid, "OK", exp, pt));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ValidateResponse(false, null, e.getClass().getSimpleName(), 0L,""));
        }
    }

    @GetMapping("/time")
    public long now() { return Instant.now().getEpochSecond(); }
}
