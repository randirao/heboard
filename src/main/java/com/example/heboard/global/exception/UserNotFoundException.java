package com.example.heboard.global.exception;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 * - User ID가 유효하지 않은 경우
 * - 로그아웃 시 존재하지 않는 사용자로 요청한 경우
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
