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

    /**
     * 로그아웃 API 엔드포인트
     *
     * 흐름:
     * 1. 요청 헤더에서 User-Id를 추출 (인증 필터에서 JWT 검증 후 설정)
     * 2. AuthService를 호출하여 로그아웃 처리
     * 3. DB에 저장된 Refresh Token을 삭제하여 무효화
     * 4. 성공 응답 반환
     *
     * 예외 상황:
     * - User-Id 헤더가 없는 경우: 400 Bad Request
     * - 유효하지 않은 User ID: 404 Not Found (UserNotFoundException)
     * - 이미 로그아웃된 경우: 정상 처리 (중복 로그아웃 허용)
     *
     * @param userId 로그아웃할 사용자 ID (헤더에서 전달)
     * @return 로그아웃 성공 응답
     */
    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화하여 로그아웃합니다")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("User-Id") Long userId) {
        // 로그아웃 서비스 호출
        authService.logout(userId);
        return ApiResponse.success("로그아웃 성공", null);
    }
}