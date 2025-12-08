package com.example.heboard.domain.article.exception;

/**
 * 게시글을 찾을 수 없을 때 발생하는 예외
 */
public class ArticleNotFoundException extends ArticleException {
    public ArticleNotFoundException() {
        super(ArticleErrorCode.ARTICLE_NOT_FOUND);
    }
}
