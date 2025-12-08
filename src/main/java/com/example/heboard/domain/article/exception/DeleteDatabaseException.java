package com.example.heboard.domain.article.exception;

/**
 * 게시글 삭제 중 DB 오류 발생 시 사용
 */
public class DeleteDatabaseException extends ArticleException {
    public DeleteDatabaseException(Throwable cause) {
        super(ArticleErrorCode.DB_ERROR_DELETE, cause);
    }
}
