package com.example.heboard.domain.comment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode {
    INVALID_ARTICLE_ID("INVALID_ARTICLE_ID", "유효한 articleId가 필요합니다."),
    INVALID_COMMENT_CONTENT("INVALID_COMMENT_CONTENT", "댓글 내용은 비어 있을 수 없습니다."),
    COMMENT_DB_ERROR("DB_ERROR", "댓글 저장 중 문제가 발생했습니다.");

    private final String code;
    private final String message;
}
