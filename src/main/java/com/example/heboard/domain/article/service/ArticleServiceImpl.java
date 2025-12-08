package com.example.heboard.domain.article.service;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleResponse;
import com.example.heboard.domain.article.entity.Article;
import com.example.heboard.domain.article.exception.ArticleErrorCode;
import com.example.heboard.domain.article.exception.ArticleException;
import com.example.heboard.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    /**
     * 게시글을 생성하고 저장한다.
     */
    @Transactional
    @Override
    public ArticleResponse createArticle(ArticleCreateRequest request, Long writerId, String writerName) {
        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(writerId)
                .writerName(writerName)
                .build();

        try {
            Article saved = articleRepository.save(article);
            log.info("게시글 저장 성공: id={}, writerId={}", saved.getId(), writerId);
            return ArticleResponse.from(saved);
        } catch (DataAccessException e) {
            log.error("게시글 저장 실패", e);
            throw new ArticleException(ArticleErrorCode.DB_ERROR, e);
        }
    }
}
