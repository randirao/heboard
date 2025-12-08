package com.example.heboard.domain.article.exception;

/**
 * 작성자 불일치 시 발생하는 예외
 */
public class ForbiddenException extends ArticleException {
    public ForbiddenException() {
        super(ArticleErrorCode.FORBIDDEN);
    }
}
