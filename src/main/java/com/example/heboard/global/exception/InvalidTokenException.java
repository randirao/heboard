package com.example.heboard.global.exception;

/**
 * 유효하지 않은 토큰 예외
 * - 토큰이 만료되었거나, 형식이 잘못된 경우
 * - 로그아웃 시 이미 무효화된 토큰으로 요청한 경우
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
