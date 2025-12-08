package com.example.heboard.domain.article.model;

import com.example.heboard.domain.article.exception.ArticleErrorCode;
import com.example.heboard.domain.article.exception.ArticleException;

import java.util.Locale;

public enum ArticleSortType {
    LATEST,
    VIEWS,
    COMMENTS;

    public static ArticleSortType from(String value) {
        if (value == null || value.isBlank()) {
            return LATEST;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "latest" -> LATEST;
            case "views" -> VIEWS;
            case "comments" -> COMMENTS;
            default -> throw new ArticleException(ArticleErrorCode.INVALID_SORT_OPTION);
        };
    }
}
