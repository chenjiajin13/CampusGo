package com.campusgo.service;

import com.campusgo.config.JwtProps;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProps props;

    private SecretKey key() { return Keys.hmacShaKeyFor(props.getSecret().getBytes()); }

    public String issueAccessToken(Long userId, String pt,int minutes) {
        return issue(userId, pt,"access", minutes);
    }
    public String issueRefreshToken(Long userId,String pt,int minutes) {
        return issue(userId, pt,"refresh", minutes);
    }

    private String issue(Long userId, String pt,String type, int minutes) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(minutes * 60L);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(props.getIssuer())
                .claim("type", type)
                .claim('pt',pt)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .requireIssuer(props.getIssuer())
                .build()
                .parseClaimsJws(token);
    }

    public String tokenType(Jws<Claims> jws) {
        Object t = jws.getBody().get("type");
        return t == null ? "" : t.toString();
    }

    public String principalType(Jws<Claims> jws) {
        Object pt = jws.getBody().get("pt");
        return pt == null ? "" : pt.toString();
    }
}
