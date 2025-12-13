package com.example.heboard.domain.comment.dto;

import com.example.heboard.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String content;
    private WriterResponse writer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .articleId(comment.getArticle().getId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .content(comment.getContent())
                .writer(new WriterResponse(comment.getWriterId(), comment.getWriterName()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
