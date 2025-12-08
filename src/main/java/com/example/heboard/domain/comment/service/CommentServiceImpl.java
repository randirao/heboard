package com.example.heboard.domain.comment.service;

import com.example.heboard.domain.article.entity.Article;
import com.example.heboard.domain.article.exception.ArticleNotFoundException;
import com.example.heboard.domain.article.repository.ArticleRepository;
import com.example.heboard.domain.comment.dto.CommentCreateRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CommentResponse updateComment(Long articleId, Long commentId, Long userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findByIdAndArticleId(commentId, articleId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getWriterId().equals(userId)) {
            throw new CommentForbiddenException();
        }

        comment.updateContent(request.getContent());

        try {
            Comment saved = commentRepository.save(comment);
            log.info("댓글 수정 성공: id={}, articleId={}, writerId={}", saved.getId(), articleId, userId);
            return CommentResponse.from(saved);
        } catch (DataAccessException e) {
            log.error("댓글 수정 실패", e);
            throw new CommentDatabaseException(e);
        }
    }
}
