package com.example.heboard.domain.article.service;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticlePreviewResponse;
import com.example.heboard.domain.article.dto.ArticleResponse;
import com.example.heboard.domain.article.dto.ArticleUpdateRequest;
import com.example.heboard.domain.article.dto.CursorPageResponse;
import com.example.heboard.domain.article.entity.Article;
import com.example.heboard.domain.article.exception.ArticleErrorCode;
import com.example.heboard.domain.article.exception.ArticleException;
import com.example.heboard.domain.article.exception.ArticleListDatabaseException;
import com.example.heboard.domain.article.exception.ArticleReadDatabaseException;
import com.example.heboard.domain.article.exception.ArticleNotFoundException;
import com.example.heboard.domain.article.exception.DeleteDatabaseException;
import com.example.heboard.domain.article.exception.DeleteForbiddenException;
import com.example.heboard.domain.article.exception.DatabaseException;
import com.example.heboard.domain.article.exception.ForbiddenException;
import com.example.heboard.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
            throw new DatabaseException(e);
        }
    }

    /**
     * 게시글을 수정한다.
     */
    @Transactional
    @Override
    public ArticleResponse updateArticle(Long articleId, ArticleUpdateRequest request, Long writerId, String writerName) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        if (!article.getWriterId().equals(writerId)) {
            throw new ForbiddenException();
        }

        article.update(request.getTitle(), request.getContent());

        try {
            Article saved = articleRepository.save(article);
            log.info("게시글 수정 성공: id={}, writerId={}", saved.getId(), writerId);
            return ArticleResponse.from(saved);
        } catch (DataAccessException e) {
            log.error("게시글 수정 실패", e);
            throw new DatabaseException(e);
        }
    }

    /**
     * 게시글을 삭제한다.
     */
    @Transactional
    @Override
    public void deleteArticle(Long articleId, Long requesterId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        if (!article.getWriterId().equals(requesterId)) {
            throw new DeleteForbiddenException();
        }

        try {
            articleRepository.delete(article);
            log.info("게시글 삭제 성공: id={}, requesterId={}", articleId, requesterId);
        } catch (DataAccessException e) {
            log.error("게시글 삭제 실패", e);
            throw new DeleteDatabaseException(e);
        }
    }

    /**
     * 게시글을 단건 조회한다.
     */
    @Transactional(readOnly = true)
    @Override
    public ArticleResponse getArticleById(Long articleId) {
        try {
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(ArticleNotFoundException::new);
            return ArticleResponse.from(article);
        } catch (DataAccessException e) {
            log.error("게시글 조회 실패", e);
            throw new ArticleReadDatabaseException(e);
        }
    }

    /**
     * 커서 기반 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    @Override
    public CursorPageResponse getArticles(Long lastId, int size, List<String> searchTypes, String keyword) {
        // size 검증
        if (size < 1 || size > 50) {
            throw new ArticleException(ArticleErrorCode.INVALID_SIZE);
        }

        // lastId 검증
        if (lastId != null && lastId < 1) {
            throw new ArticleException(ArticleErrorCode.INVALID_LAST_ID);
        }

        // 검색 파라미터 검증 및 정규화
        boolean hasSearch = keyword != null && !keyword.trim().isEmpty();
        boolean hasSearchType = searchTypes != null && !searchTypes.isEmpty();

        if (hasSearch || hasSearchType) {
            // 둘 중 하나만 있는 경우 에러 처리
            if (!hasSearchType) {
                throw new ArticleException(ArticleErrorCode.INVALID_SEARCH_TYPE);
            }
            if (!hasSearch) {
                throw new ArticleException(ArticleErrorCode.INVALID_KEYWORD);
            }
        }

        // lower-case 타입 목록으로 정규화
        List<String> normalizedTypes = searchTypes == null ? List.of() :
                searchTypes.stream().map(String::toLowerCase).toList();

        boolean searchTitle = normalizedTypes.contains("title");
        boolean searchContent = normalizedTypes.contains("content");
        boolean searchAuthor = normalizedTypes.contains("author");

        // 검색 타입에 잘못된 값이 섞여 있다면 에러
        if (hasSearchType && !(searchTitle || searchContent || searchAuthor)) {
            throw new ArticleException(ArticleErrorCode.INVALID_SEARCH_TYPE);
        }

        try {
            PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
            List<Article> articles;

            if (hasSearch && hasSearchType) {
                articles = articleRepository.findArticlesWithCursorAndSearch(
                        lastId,
                        pageable,
                        "%" + keyword.trim() + "%",
                        searchTitle,
                        searchContent,
                        searchAuthor
                );
            } else {
                articles = articleRepository.findArticlesWithCursor(lastId, pageable);
            }

            List<ArticlePreviewResponse> previews = articles.stream()
                    .map(ArticlePreviewResponse::from)
                    .collect(Collectors.toList());

            Long nextCursor = previews.isEmpty() ? null : previews.get(previews.size() - 1).getArticleId();
            boolean hasNext = previews.size() == size;

            return CursorPageResponse.builder()
                    .articles(previews)
                    .nextCursor(nextCursor)
                    .hasNext(hasNext)
                    .build();
        } catch (DataAccessException e) {
            log.error("게시글 목록 조회 실패", e);
            throw new ArticleListDatabaseException(e);
        }
    }
}
