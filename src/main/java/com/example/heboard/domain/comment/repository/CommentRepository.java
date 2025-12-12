package com.example.heboard.domain.comment.repository;

import com.example.heboard.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글과 댓글 ID로 댓글을 조회한다.
     */
    java.util.Optional<Comment> findByIdAndArticleId(Long commentId, Long articleId);

    /**
     * 게시글의 댓글을 페이지로 조회한다.
     */
    org.springframework.data.domain.Page<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId, org.springframework.data.domain.Pageable pageable);

    long countByArticleId(Long articleId);

    @org.springframework.data.jpa.repository.Query("select c.article.id as articleId, count(c) as count from Comment c where c.article.id in :articleIds group by c.article.id")
    java.util.List<CommentCount> countByArticleIds(java.util.List<Long> articleIds);

    interface CommentCount {
        Long getArticleId();
        Long getCount();
    }
}
