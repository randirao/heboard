package com.example.heboard.domain.article.controller;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleDeleteResponse;
import com.example.heboard.domain.article.dto.ArticleResponse;
import com.example.heboard.domain.article.dto.ArticleUpdateRequest;
import com.example.heboard.domain.article.dto.CursorPageResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 게시글 수정
     */
    @Operation(summary = "게시글 수정", description = "작성자 본인만 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArticleResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "articleId": 7,
                                      "title": "수정된 제목",
                                      "content": "수정된 내용",
                                      "writerId": 3,
                                      "writerName": "홍길동",
                                      "updatedAt": "2025-12-08T14:22:33"
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
            @ApiResponse(responseCode = "403", description = "작성자 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "FORBIDDEN",
                                      "message": "작성자만 게시글을 수정할 수 있습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "ARTICLE_NOT_FOUND",
                                      "message": "존재하지 않는 게시글입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "INVALID_REQUEST",
                                      "message": "제목과 내용을 올바르게 입력해주세요."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "DB_ERROR",
                                      "message": "게시글 수정 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable("id") Long articleId,
            @Valid @RequestBody ArticleUpdateRequest request
    ) {
        JwtUserPrincipal principal = getPrincipal();

        ArticleResponse response = articleService.updateArticle(
                articleId,
                request,
                principal.getUserId(),
                principal.getNickname()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제
     */
    @Operation(summary = "게시글 삭제", description = "작성자 본인만 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArticleDeleteResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "게시글이 성공적으로 삭제되었습니다.",
                                      "articleId": 7
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
            @ApiResponse(responseCode = "403", description = "작성자 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "FORBIDDEN",
                                      "message": "작성자만 게시글을 삭제할 수 있습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "ARTICLE_NOT_FOUND",
                                      "message": "존재하지 않는 게시글입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "DB_ERROR",
                                      "message": "게시글 삭제 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeleteResponse> deleteArticle(@PathVariable("id") Long articleId) {
        JwtUserPrincipal principal = getPrincipal();
        articleService.deleteArticle(articleId, principal.getUserId());
        ArticleDeleteResponse response = new ArticleDeleteResponse("게시글이 성공적으로 삭제되었습니다.", articleId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     */
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArticleResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "articleId": 5,
                                      "title": "게시글 제목",
                                      "content": "게시글 내용",
                                      "writerId": 3,
                                      "writerName": "홍길동",
                                      "createdAt": "2025-12-08T13:45:12",
                                      "updatedAt": "2025-12-08T14:10:00",
                                      "viewCount": 123,
                                      "commentCount": 4
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "ARTICLE_NOT_FOUND",
                                      "message": "존재하지 않는 게시글입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID 형식",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "INVALID_PATH_VARIABLE",
                                      "message": "잘못된 게시글 ID 형식입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "DB_ERROR",
                                      "message": "게시글 조회 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable("id") Long articleId) {
        ArticleResponse response = articleService.getArticleById(articleId);
        return ResponseEntity.ok(response);
    }

    /**
     * 커서 기반 게시글 목록 조회
     */
    @Operation(summary = "게시글 목록 조회(커서 기반)", description = "lastId 기준 최신순으로 size만큼 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursorPageResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "articles": [
                                        {
                                          "articleId": 101,
                                          "title": "게시글 제목",
                                          "contentPreview": "내용 미리보기 50자...",
                                          "writerId": 3,
                                          "writerName": "홍길동",
                                          "createdAt": "2025-12-08T12:34:56"
                                        }
                                      ],
                                      "nextCursor": 98,
                                      "hasNext": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 파라미터",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "INVALID_SIZE",
                                      "message": "size는 1에서 50 사이여야 합니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "DB_ERROR",
                                      "message": "게시글 조회 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @GetMapping
    public ResponseEntity<CursorPageResponse> getArticles(
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        CursorPageResponse response = articleService.getArticles(lastId, size);
        return ResponseEntity.ok(response);
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
