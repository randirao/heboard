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
}
