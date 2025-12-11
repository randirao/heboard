package com.example.heboard.domain.user.service;

import com.example.heboard.domain.auth.dto.SignupRequest;
import com.example.heboard.domain.auth.dto.SignupResponse;
import com.example.heboard.domain.auth.service.EmailVerificationService;
import com.example.heboard.domain.user.entity.User;
import com.example.heboard.domain.user.repository.UserRepository;
import com.example.heboard.global.exception.DuplicateEmailException;
import com.example.heboard.global.exception.DuplicateNicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.info("회원가입 시도");

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("이메일 중복 감지");
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            log.warn("닉네임 중복 감지");
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: userId={}", savedUser.getId());

        boolean mailSent = emailVerificationService.sendVerificationEmail(savedUser);
        log.info("이메일 인증 메일 발송 결과: userId={}, sent={}", savedUser.getId(), mailSent);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                savedUser.isEmailVerified(),
                savedUser.getVerifiedAt(),
                mailSent
        );
    }

    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }
}
