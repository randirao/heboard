package com.example.heboard.domain.comment.exception;

/**
 * 댓글 저장 중 DB 오류 발생 시 사용
 */
public class CommentDatabaseException extends CommentException {
    public CommentDatabaseException(Throwable cause) {
        super(CommentErrorCode.COMMENT_DB_ERROR, cause);
    }
}
