package com.example.heboard.domain.article.service;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleResponse;
import com.example.heboard.domain.article.dto.ArticleUpdateRequest;

public interface ArticleService {

    /**
     * 게시글 생성
     *
     * @param request   게시글 생성 요청 정보
     * @param writerId  작성자 ID
     * @param writerName 작성자 닉네임
     * @return 생성된 게시글 응답
     */
    ArticleResponse createArticle(ArticleCreateRequest request, Long writerId, String writerName);

    /**
     * 게시글 수정
     *
     * @param articleId  수정할 게시글 ID
     * @param request    수정 요청 DTO
     * @param writerId   요청자 ID
     * @param writerName 요청자 닉네임
     * @return 수정된 게시글 응답
     */
    ArticleResponse updateArticle(Long articleId, ArticleUpdateRequest request, Long writerId, String writerName);
}
