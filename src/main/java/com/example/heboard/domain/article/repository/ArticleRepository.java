package com.example.heboard.domain.article.repository;

import com.example.heboard.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 커서 기반 조회: lastId가 없으면 최신부터, 있으면 해당 ID 미만을 최신순으로 조회
     */
    @Query("""
        SELECT a FROM Article a
        WHERE (:lastId IS NULL OR a.id < :lastId)
        ORDER BY a.id DESC
        """)
    List<Article> findArticlesWithCursor(@Param("lastId") Long lastId, Pageable pageable);

    /**
     * 커서 + 검색 조합 조회
     * - searchTitle/content/author 중 true 인 필드에 대해 OR 조건으로 부분 일치 검색
     */
    @Query("""
        SELECT a FROM Article a
        WHERE (:lastId IS NULL OR a.id < :lastId)
          AND (
              (:searchTitle = true AND LOWER(a.title) LIKE LOWER(:keyword))
              OR (:searchContent = true AND LOWER(a.content) LIKE LOWER(:keyword))
              OR (:searchAuthor = true AND LOWER(a.writerName) LIKE LOWER(:keyword))
          )
        ORDER BY a.id DESC
        """)
    List<Article> findArticlesWithCursorAndSearch(
            @Param("lastId") Long lastId,
            Pageable pageable,
            @Param("keyword") String keyword,
            @Param("searchTitle") boolean searchTitle,
            @Param("searchContent") boolean searchContent,
            @Param("searchAuthor") boolean searchAuthor
    );
}
