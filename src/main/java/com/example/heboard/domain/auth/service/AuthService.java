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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        log.info("로그인 성공: userId={}", user.getId());

        TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);
        UserInfo userInfo = new UserInfo(user.getId(), user.getNickname(), user.getEmail());

        return new LoginResponse(tokenInfo, userInfo);
    }
}
