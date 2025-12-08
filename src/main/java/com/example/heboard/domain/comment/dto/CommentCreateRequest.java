package com.example.heboard.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "유효한 articleId가 필요합니다.")
    @Positive(message = "유효한 articleId가 필요합니다.")
    private Long articleId;

    @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
    private String content;
}
