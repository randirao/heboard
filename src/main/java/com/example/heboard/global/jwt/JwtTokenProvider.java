package com.example.heboard.global.jwt;

import com.example.heboard.domain.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        log.debug("Access Token 생성: userId={}, expiration={}", user.getId(), expiration);
        return token;
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        log.debug("Refresh Token 생성: userId={}, expiration={}", user.getId(), expiration);
        return token;
    }
}
