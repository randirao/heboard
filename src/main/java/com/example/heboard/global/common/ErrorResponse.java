package com.example.heboard.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "성공 여부", example = "false")
    private final boolean success;

    @Schema(description = "에러 코드", example = "CONFLICT")
    private final String error;

    @Schema(description = "에러 메시지", example = "요청이 충돌했습니다.")
    private final String message;

    public static ErrorResponse of(String message) {
        return new ErrorResponse(false, null, message);
    }

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(false, error, message);
    }
}
