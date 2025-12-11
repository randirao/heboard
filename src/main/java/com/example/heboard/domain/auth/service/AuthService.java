package com.example.heboard.domain.auth.service;

import com.example.heboard.domain.auth.dto.LoginRequest;
import com.example.heboard.domain.auth.dto.LoginResponse;
import com.example.heboard.domain.auth.dto.TokenInfo;
import com.example.heboard.domain.user.dto.UserInfo;
import com.example.heboard.domain.user.entity.User;
import com.example.heboard.domain.user.repository.UserRepository;
import com.example.heboard.global.exception.AuthenticationException;
import com.example.heboard.global.exception.InvalidPasswordException;
import com.example.heboard.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. 사용자 인증: 이메일 또는 닉네임으로 사용자 조회
        String identifier = request.getIdentifier();
        User user = userRepository.findByEmailOrNickname(identifier, identifier)
                .orElseThrow(() -> new AuthenticationException("이메일/닉네임 또는 비밀번호가 올바르지 않습니다"));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("이메일/닉네임 또는 비밀번호가 올바르지 않습니다");
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // 4. Refresh Token을 DB에 저장 (로그아웃 시 무효화를 위해)
        jwtTokenProvider.saveRefreshToken(user.getId(), refreshToken);

        log.info("로그인 성공: userId={}", user.getId());

        // 5. 응답 데이터 생성
        TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);
        UserInfo userInfo = new UserInfo(user.getId(), user.getNickname(), user.getEmail());

        return new LoginResponse(tokenInfo, userInfo);
    }

    /**
     * 로그아웃 - Refresh Token 무효화
     *
     * 로그아웃 시 DB에 저장된 Refresh Token을 삭제하여 무효화
     * - User ID로 저장된 Refresh Token 조회 및 삭제
     * - 토큰이 없어도 예외를 발생시키지 않음 (이미 로그아웃된 상태로 간주)
     *
     * @param userId 로그아웃할 사용자 ID
     */
    @Transactional
    public void logout(Long userId) {
        // DB에서 Refresh Token 삭제
        jwtTokenProvider.invalidateRefreshToken(userId);
        log.info("로그아웃 성공: userId={}", userId);
    }
}
