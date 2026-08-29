package com.artisanplatform.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * Standard response envelope used by every controller across every domain
 * module, per the "API naming and response conventions should remain
 * consistent across domains" principle (source §30, generalized).
 *
 * <p>Success responses set {@code data} and leave {@code error} null.
 * Error responses (see GlobalExceptionHandler in each module) set
 * {@code error} and leave {@code data} null.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ApiError error;
    private final Instant timestamp;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ApiError.builder().code(code).message(message).path(path).build())
                .timestamp(Instant.now())
                .build();
    }

    @Getter
    @Builder
    public static class ApiError {
        private final String code;
        private final String message;
        private final String path;
    }
}
