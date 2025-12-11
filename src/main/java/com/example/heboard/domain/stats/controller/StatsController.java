package com.example.heboard.domain.stats.controller;

import com.example.heboard.domain.article.service.ArticleService;
import com.example.heboard.domain.user.service.UserService;
import com.example.heboard.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Stats", description = "통계 API")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserService userService;
    private final ArticleService articleService;

    @Operation(summary = "통계 조회", description = "사이트 통계 정보를 조회합니다")
    @GetMapping
    public ApiResponse<Map<String, Object>> getStats() {
        long userCount = userService.getUserCount();
        long articleCount = articleService.getArticleCount();

        Map<String, Object> stats = Map.of(
                "userCount", userCount,
                "articleCount", articleCount
        );

        return ApiResponse.success("통계 조회 성공", stats);
    }
}
