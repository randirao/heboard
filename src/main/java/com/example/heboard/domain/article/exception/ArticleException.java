package com.example.heboard.domain.article.exception;

import lombok.Getter;

@Getter
public class ArticleException extends RuntimeException {

    private final ArticleErrorCode errorCode;

    public ArticleException(ArticleErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ArticleException(ArticleErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
