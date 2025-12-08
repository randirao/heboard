package com.example.heboard.domain.article.exception;

/**
 * 게시글 조회 중 DB 오류 발생 시 사용
 */
public class ArticleReadDatabaseException extends ArticleException {
    public ArticleReadDatabaseException(Throwable cause) {
        super(ArticleErrorCode.DB_ERROR_READ, cause);
    }
}
