package com.example.heboard.domain.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleDeleteResponse {
    private final String message;
    private final Long articleId;
}
