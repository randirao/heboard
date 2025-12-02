package com.example.heboard.domain.user.service;

import com.example.heboard.domain.auth.dto.SignupRequest;
import com.example.heboard.domain.auth.dto.SignupResponse;
import com.example.heboard.domain.user.entity.User;
import com.example.heboard.domain.user.repository.UserRepository;
import com.example.heboard.global.exception.DuplicateEmailException;
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

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.info("회원가입 시도: email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("이메일 중복: {}", request.getEmail());
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }
}
