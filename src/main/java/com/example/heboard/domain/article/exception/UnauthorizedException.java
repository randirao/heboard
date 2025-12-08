package com.example.heboard.domain.article.exception;

/**
 * 인증되지 않은 사용자가 요청할 때 발생하는 예외
 */
public class UnauthorizedException extends ArticleException {
    public UnauthorizedException() {
        super(ArticleErrorCode.UNAUTHORIZED);
    }
}
