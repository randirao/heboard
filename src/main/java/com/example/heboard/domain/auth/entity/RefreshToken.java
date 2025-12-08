package com.example.heboard.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Refresh Token 엔티티
 * - 사용자별 Refresh Token을 저장하고 관리
 * - 로그아웃 시 토큰 무효화를 위해 사용
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User ID (FK 대신 단순 ID 저장으로 간단하게 구현)
    @Column(nullable = false, unique = true)
    private Long userId;

    // Refresh Token 값
    @Column(nullable = false, length = 500)
    private String token;

    // 토큰 만료 시간
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // 생성 시간
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RefreshToken(Long userId, String token, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * Refresh Token 업데이트
     * - 로그인 시 기존 토큰을 새 토큰으로 갱신
     */
    public void updateToken(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}