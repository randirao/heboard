package com.example.heboard.domain.article.exception;

/**
 * 게시글 삭제 시 작성자 불일치 예외
 */
public class DeleteForbiddenException extends ArticleException {
    public DeleteForbiddenException() {
        super(ArticleErrorCode.FORBIDDEN_DELETE);
    }
}
