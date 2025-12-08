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
}
