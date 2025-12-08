package com.example.heboard.domain.article.service;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleResponse;
import com.example.heboard.domain.article.dto.ArticleUpdateRequest;
import com.example.heboard.domain.article.dto.ArticleDeleteResponse;
import com.example.heboard.domain.article.dto.CursorPageResponse;

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

    /**
     * 게시글 삭제
     *
     * @param articleId   삭제할 게시글 ID
     * @param requesterId 요청자 ID
     */
    void deleteArticle(Long articleId, Long requesterId);

    /**
     * 게시글 상세 조회
     *
     * @param articleId 조회할 게시글 ID
     * @return 게시글 응답 DTO
     */
    ArticleResponse getArticleById(Long articleId);

    /**
     * 커서 기반 게시글 목록 조회
     *
     * @param lastId 마지막으로 조회한 게시글 ID (null이면 최신부터)
     * @param size   조회할 개수
     * @return 커서 페이지 응답
     */
    CursorPageResponse getArticles(Long lastId, int size);
}
