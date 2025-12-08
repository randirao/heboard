package com.example.heboard.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CommentPageResponse {
    private List<CommentResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
