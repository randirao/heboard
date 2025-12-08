package com.example.heboard.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "INVALID_REQUEST")
    private final String error;

    @Schema(description = "에러 메시지", example = "에러 메시지")
    private final String message;

    public static ErrorResponse of(String message) {
        return new ErrorResponse(null, message);
    }

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message);
    }
}
