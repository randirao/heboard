package com.example.heboard.domain.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUpdateRequest {

    @NotBlank(message = "제목과 내용을 올바르게 입력해주세요.")
    @Size(min = 1, max = 100, message = "제목과 내용을 올바르게 입력해주세요.")
    private String title;

    @NotBlank(message = "제목과 내용을 올바르게 입력해주세요.")
    @Size(min = 1, max = 5000, message = "제목과 내용을 올바르게 입력해주세요.")
    private String content;
}
