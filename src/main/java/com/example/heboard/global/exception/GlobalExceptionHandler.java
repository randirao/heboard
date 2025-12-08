package com.example.heboard.global.exception;

import com.example.heboard.domain.article.dto.ArticleCreateRequest;
import com.example.heboard.domain.article.dto.ArticleUpdateRequest;
import com.example.heboard.domain.article.exception.ArticleErrorCode;
import com.example.heboard.domain.article.exception.ArticleException;
import com.example.heboard.domain.comment.dto.CommentCreateRequest;
import com.example.heboard.domain.comment.dto.CommentUpdateRequest;
import com.example.heboard.domain.comment.exception.CommentErrorCode;
import com.example.heboard.domain.comment.exception.CommentException;
import com.example.heboard.global.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ArticleException.class)
    public ResponseEntity<ErrorResponse> handleArticleException(ArticleException e) {
        ArticleErrorCode errorCode = e.getErrorCode();
        HttpStatus status = switch (errorCode) {
            case UNAUTHORIZED, INVALID_TOKEN -> HttpStatus.UNAUTHORIZED;
            case INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
            case INVALID_SIZE, INVALID_LAST_ID, INVALID_SEARCH_TYPE, INVALID_KEYWORD, INVALID_SORT_OPTION -> HttpStatus.BAD_REQUEST;
            case DB_ERROR, DB_ERROR_DELETE -> HttpStatus.INTERNAL_SERVER_ERROR;
            case DB_ERROR_READ -> HttpStatus.INTERNAL_SERVER_ERROR;
            case ARTICLE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case FORBIDDEN, FORBIDDEN_DELETE -> HttpStatus.FORBIDDEN;
        };
        log.warn("게시글 예외 발생: {}", errorCode.getMessage());
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> handleCommentException(CommentException e) {
        CommentErrorCode errorCode = e.getErrorCode();
        HttpStatus status = switch (errorCode) {
            case INVALID_ARTICLE_ID, INVALID_COMMENT_CONTENT, INVALID_CONTENT -> HttpStatus.BAD_REQUEST;
            case COMMENT_DB_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case COMMENT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case COMMENT_FORBIDDEN -> HttpStatus.FORBIDDEN;
        };
        log.warn("댓글 예외 발생: {}", errorCode.getMessage());
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmailException(DuplicateEmailException e) {
        log.warn("이메일 중복");
        return ErrorResponse.of("CONFLICT", "이미 사용 중인 이메일입니다.");
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateNicknameException(DuplicateNicknameException e) {
        log.warn("닉네임 중복");
        return ErrorResponse.of("CONFLICT", "이미 사용 중인 닉네임입니다.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("ResourceNotFoundException: {}", e.getMessage());
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn("인증 실패");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        log.warn("유효하지 않은 토큰: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidPasswordException(InvalidPasswordException e) {
        log.warn("비밀번호 불일치: {}", e.getMessage());
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.warn("사용자를 찾을 수 없음: {}", e.getMessage());
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        if (e.getBindingResult().getTarget() instanceof ArticleCreateRequest) {
            log.warn("게시글 생성 요청 검증 실패");
            return ErrorResponse.of(ArticleErrorCode.INVALID_REQUEST.getCode(),
                    ArticleErrorCode.INVALID_REQUEST.getMessage());
        }
        if (e.getBindingResult().getTarget() instanceof ArticleUpdateRequest) {
            log.warn("게시글 수정 요청 검증 실패");
            return ErrorResponse.of(ArticleErrorCode.INVALID_REQUEST.getCode(),
                    "제목과 내용을 올바르게 입력해주세요.");
        }
        if (e.getBindingResult().getTarget() instanceof CommentCreateRequest) {
            String message = e.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField())
                    .findFirst()
                    .orElse("");
            if ("articleId".equals(message)) {
                log.warn("댓글 생성 articleId 검증 실패");
                return ErrorResponse.of(CommentErrorCode.INVALID_ARTICLE_ID.getCode(),
                        CommentErrorCode.INVALID_ARTICLE_ID.getMessage());
            }
            log.warn("댓글 생성 content 검증 실패");
            return ErrorResponse.of(CommentErrorCode.INVALID_COMMENT_CONTENT.getCode(),
                    CommentErrorCode.INVALID_COMMENT_CONTENT.getMessage());
        }
        if (e.getBindingResult().getTarget() instanceof CommentUpdateRequest) {
            log.warn("댓글 수정 content 검증 실패");
            return ErrorResponse.of(CommentErrorCode.INVALID_CONTENT.getCode(),
                    CommentErrorCode.INVALID_CONTENT.getMessage());
        }

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("입력값 검증 실패");
        log.warn("유효성 검증 실패: {}", message);
        return ErrorResponse.of(message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("데이터 무결성 충돌: {}", e.getMessage());
        return ErrorResponse.of("CONFLICT", "데이터가 충돌했습니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("잘못된 PathVariable 타입: {}", e.getMessage());
        String name = e.getName();
        if ("lastId".equals(name)) {
            return ErrorResponse.of("INVALID_LAST_ID", "lastId는 양의 정수여야 합니다.");
        }
        if ("size".equals(name)) {
            return ErrorResponse.of("INVALID_SIZE", "size는 1에서 50 사이여야 합니다.");
        }
        return ErrorResponse.of("INVALID_PATH_VARIABLE", "잘못된 게시글 ID 형식입니다.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("예상치 못한 오류 발생", e);
        return ErrorResponse.of("내부서버 오류입니다.");
    }
}
