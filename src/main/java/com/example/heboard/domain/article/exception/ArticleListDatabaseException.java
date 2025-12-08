package com.example.heboard.domain.article.exception;

/**
 * 게시글 목록 조회 중 DB 오류 발생 시 사용
 */
public class ArticleListDatabaseException extends ArticleException {
    public ArticleListDatabaseException(Throwable cause) {
        super(ArticleErrorCode.DB_ERROR_READ, cause);
    }
}
