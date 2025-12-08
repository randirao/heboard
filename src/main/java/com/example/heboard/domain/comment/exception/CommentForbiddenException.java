package com.example.heboard.domain.comment.exception;

/**
 * 댓글 수정 권한이 없을 때 발생하는 예외
 */
public class CommentForbiddenException extends CommentException {
    public CommentForbiddenException() {
        super(CommentErrorCode.COMMENT_FORBIDDEN);
    }
}
