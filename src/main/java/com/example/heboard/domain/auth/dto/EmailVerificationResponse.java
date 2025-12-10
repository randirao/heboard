package com.example.heboard.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmailVerificationResponse {

    private String email;
    private LocalDateTime verifiedAt;
}
