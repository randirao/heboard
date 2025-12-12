package com.example.heboard.domain.comment.service;

import com.example.heboard.domain.article.entity.Article;
import com.example.heboard.domain.article.exception.ArticleNotFoundException;
import com.example.heboard.domain.article.repository.ArticleRepository;
import com.example.heboard.domain.comment.dto.CommentCreateRequest;
import com.example.heboard.domain.comment.dto.CommentPageResponse;
import com.example.heboard.domain.comment.dto.CommentResponse;
import com.example.heboard.domain.comment.dto.CommentUpdateRequest;
import com.example.heboard.domain.comment.entity.Comment;
import com.example.heboard.domain.comment.exception.CommentDatabaseException;
import com.example.heboard.domain.comment.exception.CommentForbiddenException;
import com.example.heboard.domain.comment.exception.CommentNotFoundException;
import com.example.heboard.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    /**
     * 댓글을 생성하여 저장한다.
     */
    @Transactional
    @Override
    public CommentResponse createComment(Long userId, String userName, CommentCreateRequest request) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(ArticleNotFoundException::new);

        Comment comment = Comment.builder()
                .article(article)
                .writerId(userId)
                .writerName(userName)
                .content(request.getContent())
                .build();

        try {
            Comment saved = commentRepository.save(comment);
            article.increaseCommentCount();
            articleRepository.save(article);
            log.info("댓글 저장 성공: id={}, articleId={}, writerId={}", saved.getId(), article.getId(), userId);
            return CommentResponse.from(saved);
        } catch (DataAccessException e) {
            log.error("댓글 저장 실패", e);
            throw new CommentDatabaseException(e);
        }
    }

    /**
     * 댓글을 수정한다.
     */
    @Transactional
    @Override
    public CommentResponse updateComment(Long commentId, Long userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getWriterId().equals(userId)) {
            throw new CommentForbiddenException();
        }

        comment.updateContent(request.getContent());

        try {
            Comment saved = commentRepository.save(comment);
            log.info("댓글 수정 성공: id={}, writerId={}", saved.getId(), userId);
            return CommentResponse.from(saved);
        } catch (DataAccessException e) {
            log.error("댓글 수정 실패", e);
            throw new CommentDatabaseException(e);
        }
    }

    /**
     * 댓글을 삭제한다.
     */
    @Transactional
    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getWriterId().equals(userId)) {
            throw new CommentForbiddenException();
        }

        try {
            Article article = comment.getArticle();
            commentRepository.delete(comment);
            if (article != null) {
                article.decreaseCommentCount();
                articleRepository.save(article);
            }
            log.info("댓글 삭제 성공: id={}, writerId={}", commentId, userId);
        } catch (DataAccessException e) {
            log.error("댓글 삭제 실패", e);
            throw new CommentDatabaseException(e);
        }
    }

    /**
     * 게시글의 댓글을 페이지로 조회한다.
     */
    @Transactional(readOnly = true)
    @Override
    public CommentPageResponse getComments(Long articleId, int page, int size) {
        if (page < 0) page = 0;
        if (size < 1 || size > 50) size = 10;

        // 게시글 존재 여부 확인
        if (!articleRepository.existsById(articleId)) {
            throw new ArticleNotFoundException();
        }

        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<Comment> commentPage = commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId, pageable);

            return CommentPageResponse.builder()
                    .content(commentPage.stream()
                            .map(CommentResponse::from)
                            .collect(Collectors.toList()))
                    .page(commentPage.getNumber())
                    .size(commentPage.getSize())
                    .totalElements(commentPage.getTotalElements())
                    .totalPages(commentPage.getTotalPages())
                    .build();
        } catch (DataAccessException e) {
            log.error("댓글 목록 조회 실패", e);
            throw new CommentDatabaseException(e);
        }
    }
}
