package com.example.heboard.domain.article.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArticleErrorCode {
    UNAUTHORIZED("UNAUTHORIZED", "로그인이 필요합니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 인증 정보입니다."),
    INVALID_REQUEST("INVALID_REQUEST", "제목과 내용을 모두 입력해주세요."),
    INVALID_SIZE("INVALID_SIZE", "size는 1에서 50 사이여야 합니다."),
    INVALID_LAST_ID("INVALID_LAST_ID", "lastId는 양의 정수여야 합니다."),
    INVALID_SEARCH_TYPE("INVALID_SEARCH_TYPE", "searchType 파라미터는 title, content, author 만 사용할 수 있습니다."),
    INVALID_KEYWORD("INVALID_KEYWORD", "keyword 파라미터는 비어 있을 수 없습니다."),
    INVALID_SORT_OPTION("INVALID_SORT_OPTION", "sort 파라미터는 latest, views, comments 중 하나여야 합니다."),
    DB_ERROR("DB_ERROR", "게시글 저장 중 문제가 발생했습니다."),
    DB_ERROR_READ("DB_ERROR", "게시글 조회 중 문제가 발생했습니다."),
    ARTICLE_NOT_FOUND("ARTICLE_NOT_FOUND", "존재하지 않는 게시글입니다."),
    FORBIDDEN("FORBIDDEN", "작성자만 게시글을 수정할 수 있습니다."),
    FORBIDDEN_DELETE("FORBIDDEN", "작성자만 게시글을 삭제할 수 있습니다."),
    DB_ERROR_DELETE("DB_ERROR", "게시글 삭제 중 문제가 발생했습니다.");

    private final String code;
    private final String message;
}
