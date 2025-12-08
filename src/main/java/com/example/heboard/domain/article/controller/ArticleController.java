package com.example.heboard.domain.article.controller;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleResponse;
import com.example.heboard.domain.article.exception.ArticleErrorCode;
import com.example.heboard.domain.article.exception.ArticleException;
import com.example.heboard.domain.article.service.ArticleService;
import com.example.heboard.security.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Article", description = "게시글 API")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 게시글 작성
     */
    @Operation(summary = "게시글 작성", description = "로그인 사용자가 게시글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArticleResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "articleId": 10,
                                      "title": "작성 완료된 제목",
                                      "content": "작성된 내용",
                                      "writerId": 3,
                                      "writerName": "홍길동",
                                      "createdAt": "2025-12-08T12:34:56"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "UNAUTHORIZED",
                                      "message": "로그인이 필요합니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "FORBIDDEN",
                                      "message": "접근 권한이 없습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "INVALID_REQUEST",
                                      "message": "제목과 내용을 모두 입력해주세요."
                                    }
                                    """))),
            @ApiResponse(responseCode = "409", description = "충돌",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "CONFLICT",
                                      "message": "중복 요청입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "DB_ERROR",
                                      "message": "게시글 저장 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleCreateRequest request) {
        JwtUserPrincipal principal = getPrincipal();

        ArticleResponse response = articleService.createArticle(
                request,
                principal.getUserId(),
                principal.getNickname()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private JwtUserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ArticleException(ArticleErrorCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserPrincipal jwtUserPrincipal
                && StringUtils.hasText(jwtUserPrincipal.getNickname())) {
            return jwtUserPrincipal;
        }
        throw new ArticleException(ArticleErrorCode.INVALID_TOKEN);
    }
}
