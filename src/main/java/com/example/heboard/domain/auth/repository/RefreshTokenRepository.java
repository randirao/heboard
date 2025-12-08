package com.example.heboard.domain.auth.repository;

import com.example.heboard.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Refresh Token 레포지토리
 * - Refresh Token의 저장, 조회, 삭제 기능 제공
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * User ID로 Refresh Token 조회
     * @param userId 사용자 ID
     * @return Refresh Token (Optional)
     */
    Optional<RefreshToken> findByUserId(Long userId);

    /**
     * User ID로 Refresh Token 삭제
     * - 로그아웃 시 사용
     * @param userId 사용자 ID
     */
    void deleteByUserId(Long userId);

    /**
     * User ID로 Refresh Token 존재 여부 확인
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByUserId(Long userId);
}
