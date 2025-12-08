package com.example.heboard.domain.comment.controller;

import com.example.heboard.domain.comment.dto.CommentCreateRequest;
import com.example.heboard.domain.comment.dto.CommentResponse;
import com.example.heboard.domain.comment.dto.CommentUpdateRequest;
import com.example.heboard.domain.comment.dto.DeleteCommentResponse;
import com.example.heboard.domain.comment.dto.CommentPageResponse;
import com.example.heboard.domain.comment.exception.CommentErrorCode;
import com.example.heboard.domain.comment.exception.CommentException;
import com.example.heboard.domain.comment.service.CommentService;
import com.example.heboard.domain.article.exception.ArticleErrorCode;
import com.example.heboard.domain.article.exception.ArticleException;
import com.example.heboard.security.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     */
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 20,
                                      "articleId": 1,
                                      "content": "댓글 내용입니다.",
                                      "writer": {
                                        "id": 3,
                                        "name": "홍길동"
                                      },
                                      "createdAt": "2025-12-02T13:22:10",
                                      "updatedAt": "2025-12-02T13:22:10"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "INVALID_COMMENT_CONTENT",
                                      "message": "댓글 내용은 비어 있을 수 없습니다."
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
                                      "message": "댓글 저장 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest request) {
        JwtUserPrincipal principal = getPrincipal();
        CommentResponse response = commentService.createComment(principal.getUserId(), principal.getNickname(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 수정
     */
    @Operation(summary = "댓글 수정", description = "작성자 본인만 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 101,
                                      "articleId": 20,
                                      "content": "수정된 댓글 내용",
                                      "writer": {
                                        "id": 10,
                                        "name": "홍길동"
                                      },
                                      "createdAt": "2025-12-08T13:11:00",
                                      "updatedAt": "2025-12-08T13:11:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "INVALID_CONTENT",
                                      "message": "content는 비어 있을 수 없습니다."
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
                                      "message": "이 댓글을 수정할 권한이 없습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "댓글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "COMMENT_NOT_FOUND",
                                      "message": "해당 댓글을 찾을 수 없습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "DB_ERROR",
                                      "message": "댓글 저장 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        JwtUserPrincipal principal = getPrincipal();
        CommentResponse response = commentService.updateComment(commentId, principal.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제", description = "댓글 작성자 본인만 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeleteCommentResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "댓글이 성공적으로 삭제되었습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "error": "UNAUTHORIZED",
                                      "message": "로그인이 필요합니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "403", description = "작성자 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "error": "FORBIDDEN",
                                      "message": "이 댓글을 수정할 권한이 없습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "댓글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "error": "COMMENT_NOT_FOUND",
                                      "message": "해당 댓글을 찾을 수 없습니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "error": "DB_ERROR",
                                      "message": "댓글 처리 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<DeleteCommentResponse> deleteComment(@PathVariable("id") Long commentId) {
        JwtUserPrincipal principal = getPrincipal();
        commentService.deleteComment(commentId, principal.getUserId());
        return ResponseEntity.ok(DeleteCommentResponse.ok());
    }

    /**
     * 게시글 댓글 목록 조회 (페이지 기반)
     */
    @Operation(summary = "게시글 댓글 목록 조회", description = "게시글 ID로 댓글을 페이지 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentPageResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "content": [
                                        {
                                          "id": 1,
                                          "articleId": 10,
                                          "content": "댓글 내용",
                                          "writer": { "id": 3, "name": "홍길동" },
                                          "createdAt": "2025-12-08T12:00:00",
                                          "updatedAt": "2025-12-08T12:00:00"
                                        }
                                      ],
                                      "page": 0,
                                      "size": 10,
                                      "totalElements": 1,
                                      "totalPages": 1
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "error": "ARTICLE_NOT_FOUND",
                                      "message": "존재하지 않는 게시글입니다."
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "error": "DB_ERROR",
                                      "message": "댓글 처리 중 문제가 발생했습니다."
                                    }
                                    """)))
    })
    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentPageResponse> getComments(
            @PathVariable("articleId") Long articleId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        CommentPageResponse response = commentService.getComments(articleId, page, size);
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
