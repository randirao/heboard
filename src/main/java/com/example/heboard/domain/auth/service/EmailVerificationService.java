package com.example.heboard.domain.auth.service;

import com.example.heboard.domain.auth.dto.EmailVerificationResponse;
import com.example.heboard.domain.auth.entity.EmailVerificationToken;
import com.example.heboard.domain.auth.repository.EmailVerificationTokenRepository;
import com.example.heboard.domain.user.entity.User;
import com.example.heboard.domain.user.repository.UserRepository;
import com.example.heboard.global.exception.EmailAlreadyVerifiedException;
import com.example.heboard.global.exception.InvalidVerificationTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Value("${app.email.verification-token-expiration-minutes:30}")
    private long tokenExpirationMinutes;

    /**
     * 인증 메일 발송 및 토큰 저장
     */
    @Transactional
    public boolean sendVerificationEmail(User user) {
        emailVerificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("[heboard] 이메일 인증을 완료해주세요");
        message.setText(buildEmailContent(token, user.getNickname(), expiresAt));

        try {
            mailSender.send(message);
            log.info("이메일 인증 메일 발송 완료: userId={}, email={}", user.getId(), user.getEmail());
            return true;
        } catch (MailException e) {
            log.error("이메일 인증 메일 발송 실패: userId={}, email={}, reason={}", user.getId(), user.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * 이메일로 재발송
     */
    @Transactional
    public boolean resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidVerificationTokenException("가입된 이메일을 찾을 수 없습니다."));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("이미 이메일 인증이 완료된 계정입니다.");
        }

        return sendVerificationEmail(user);
    }

    /**
     * 토큰 검증 및 사용자 이메일 인증 처리
     */
    @Transactional
    public EmailVerificationResponse verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("유효하지 않은 인증 링크입니다."));

        if (verificationToken.isUsed()) {
            throw new InvalidVerificationTokenException("이미 사용된 인증 링크입니다.");
        }

        if (verificationToken.isExpired()) {
            throw new InvalidVerificationTokenException("인증 링크가 만료되었습니다.");
        }

        User user = verificationToken.getUser();
        user.verifyEmail();
        verificationToken.markAsUsed();

        emailVerificationTokenRepository.save(verificationToken);
        userRepository.save(user);

        log.info("이메일 인증 성공: userId={}, email={}", user.getId(), user.getEmail());
        return new EmailVerificationResponse(user.getEmail(), user.getVerifiedAt());
    }

    private String buildEmailContent(String token, String nickname, LocalDateTime expiresAt) {
        String verificationLink = String.format("%s/?verifyToken=%s", frontendBaseUrl, token);
        String formattedExpiry = expiresAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return """
                %s님, heboard 가입을 위한 이메일 인증을 완료해주세요.

                아래 링크를 클릭하면 인증이 완료됩니다.
                인증 만료 시각: %s

                %s

                만약 인증을 요청하지 않았다면 이 메일을 무시해주세요.
                """.formatted(nickname, formattedExpiry, verificationLink);
    }
}
