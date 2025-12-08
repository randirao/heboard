package com.example.heboard.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "에러 메시지", example = "에러 메시지")
    private final String message;

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
