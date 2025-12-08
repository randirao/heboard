package com.example.heboard.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteCommentResponse {
    private final boolean success;
    private final String message;

    public static DeleteCommentResponse ok() {
        return new DeleteCommentResponse(true, "댓글이 성공적으로 삭제되었습니다.");
    }
}
