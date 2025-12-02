package com.example.heboard.domain.auth.controller;

import com.example.heboard.domain.auth.dto.LoginRequest;
import com.example.heboard.domain.auth.dto.LoginResponse;
import com.example.heboard.domain.auth.dto.SignupRequest;
import com.example.heboard.domain.auth.dto.SignupResponse;
import com.example.heboard.domain.auth.service.AuthService;
import com.example.heboard.domain.user.service.UserService;
import com.example.heboard.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입합니다")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ApiResponse.success("회원가입 성공", response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("로그인 성공", response);
    }
}