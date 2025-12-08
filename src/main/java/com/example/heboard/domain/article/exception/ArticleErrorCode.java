package com.example.heboard.domain.article.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArticleErrorCode {
    UNAUTHORIZED("UNAUTHORIZED", "로그인이 필요합니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 인증 정보입니다."),
    INVALID_REQUEST("INVALID_REQUEST", "제목과 내용을 모두 입력해주세요."),
    DB_ERROR("DB_ERROR", "게시글 저장 중 문제가 발생했습니다.");

    private final String code;
    private final String message;
}
