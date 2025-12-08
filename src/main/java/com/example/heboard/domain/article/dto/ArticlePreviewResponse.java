package com.example.heboard.domain.article.dto;

import com.example.heboard.domain.article.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ArticlePreviewResponse {
    private Long articleId;
    private String title;
    private String contentPreview;
    private Long writerId;
    private String writerName;
    private LocalDateTime createdAt;

    public static ArticlePreviewResponse from(Article article) {
        String content = article.getContent() == null ? "" : article.getContent();
        String preview = content.substring(0, Math.min(50, content.length()));

        return ArticlePreviewResponse.builder()
                .articleId(article.getId())
                .title(article.getTitle())
                .contentPreview(preview)
                .writerId(article.getWriterId())
                .writerName(article.getWriterName())
                .createdAt(article.getCreatedAt())
                .build();
    }
}
