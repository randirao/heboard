package com.example.heboard.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Date;

@Tag(name = "JWT 테스트", description = "개발용 JWT 토큰 생성 API")
@RestController
@RequestMapping("/api/auth/test")
public class JwtTestController {

    private final SecretKey key;
    private final long tokenValidity = 86400000; // 24시간

    public JwtTestController(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Operation(summary = "테스트용 JWT 토큰 생성", description = "userId로 JWT 토큰을 생성합니다. (개발용)")
    @GetMapping("/token")
    public TokenResponse generateTestToken(@RequestParam Long userId) {
        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(now))
                .expiration(new Date(now + tokenValidity))
                .signWith(key, Jwts.SIG.HS512)
                .compact();

        return new TokenResponse(token, "Bearer " + token);
    }

    record TokenResponse(String token, String authorization) {}
}
