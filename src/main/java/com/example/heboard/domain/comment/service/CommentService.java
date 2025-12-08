package com.example.heboard.domain.comment.service;

import com.example.heboard.domain.comment.dto.CommentCreateRequest;
import com.example.heboard.domain.comment.dto.CommentResponse;

public interface CommentService {

    /**
     * 댓글을 생성한다.
     *
     * @param userId  작성자 ID
     * @param userName 작성자 이름(닉네임)
     * @param request 댓글 생성 요청
     * @return 생성된 댓글 응답
     */
    CommentResponse createComment(Long userId, String userName, CommentCreateRequest request);
}
