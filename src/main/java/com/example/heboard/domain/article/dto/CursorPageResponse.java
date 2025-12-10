package com.example.heboard.domain.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CursorPageResponse {
    private List<ArticlePreviewResponse> posts;
    private Long nextCursor;
    private boolean hasMore;
}
