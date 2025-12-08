package com.example.heboard.domain.article.exception;

/**
 * 게시글 저장/수정 중 DB 오류가 발생할 때 사용
 */
public class DatabaseException extends ArticleException {
    public DatabaseException(Throwable cause) {
        super(ArticleErrorCode.DB_ERROR, cause);
    }
}
