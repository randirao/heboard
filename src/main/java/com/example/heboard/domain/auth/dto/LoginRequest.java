package com.example.heboard.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일 또는 닉네임은 필수입니다")
    private String identifier;  // 이메일 또는 닉네임

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
