package com.example.heboard.domain.article.service;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleResponse;

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
}
