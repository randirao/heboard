package com.example.heboard.global.jwt;

import com.example.heboard.domain.auth.entity.RefreshToken;
import com.example.heboard.domain.auth.repository.RefreshTokenRepository;
import com.example.heboard.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenRepository = refreshTokenRepository;
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

    /**
     * Refresh Token을 DB에 저장 또는 업데이트
     * - 로그인 시 호출되어 Refresh Token을 저장
     * - 기존 토큰이 있으면 업데이트, 없으면 새로 생성
     */
    public void saveRefreshToken(Long userId, String token) {
        // 토큰 만료 시간 계산
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration / 1000);

        // 기존 토큰 조회
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElse(null);

        if (refreshToken != null) {
            // 기존 토큰이 있으면 업데이트
            refreshToken.updateToken(token, expiresAt);
            refreshTokenRepository.save(refreshToken);
            log.debug("Refresh Token 업데이트: userId={}", userId);
        } else {
            // 새 토큰 생성
            RefreshToken newToken = RefreshToken.builder()
                    .userId(userId)
                    .token(token)
                    .expiresAt(expiresAt)
                    .build();
            refreshTokenRepository.save(newToken);
            log.debug("Refresh Token 저장: userId={}", userId);
        }
    }

    /**
     * Refresh Token 무효화
     * - 로그아웃 시 호출되어 DB에서 Refresh Token 삭제
     * - 토큰이 없어도 예외를 발생시키지 않음 (이미 로그아웃된 상태)
     */
    public void invalidateRefreshToken(Long userId) {
        // User ID로 Refresh Token 존재 여부 확인
        if (refreshTokenRepository.existsByUserId(userId)) {
            // Refresh Token 삭제
            refreshTokenRepository.deleteByUserId(userId);
            log.info("Refresh Token 삭제 완료: userId={}", userId);
        } else {
            // 토큰이 없는 경우 (이미 로그아웃되었거나, 로그인한 적 없음)
            log.warn("삭제할 Refresh Token이 없습니다: userId={}", userId);
        }
    }

    /**
     * Refresh Token 유효성 검증
     * - DB에 저장된 토큰과 비교
     * - 만료 시간 확인
     */
    public boolean validateRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElse(null);

        if (refreshToken == null) {
            log.warn("Refresh Token이 존재하지 않습니다: userId={}", userId);
            return false;
        }

        // 토큰 값 비교
        if (!refreshToken.getToken().equals(token)) {
            log.warn("Refresh Token이 일치하지 않습니다: userId={}", userId);
            return false;
        }

        // 만료 시간 확인
        if (refreshToken.isExpired()) {
            log.warn("Refresh Token이 만료되었습니다: userId={}", userId);
            return false;
        }

        return true;
    }

    /**
     * JWT 토큰 유효성 검증
     * - 토큰 서명, 만료 시간 등을 확인
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * JWT 토큰에서 User ID 추출
     */
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            log.error("JWT subject is not a valid user ID: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid user ID in token", e);
        }
    }
}
