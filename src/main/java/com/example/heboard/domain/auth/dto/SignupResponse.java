package com.example.heboard.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private Long id;
    private String email;
    private LocalDateTime createdAt;
}
