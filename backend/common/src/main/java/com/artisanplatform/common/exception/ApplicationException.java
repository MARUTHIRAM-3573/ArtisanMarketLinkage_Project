package com.artisanplatform.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for every domain-specific exception in every module (e.g.
 * {@code CatalogException}, {@code PaymentException}). Carries the HTTP
 * status and a machine-readable error code so
 * {@code GlobalExceptionHandler} in each module can translate it into a
 * consistent {@link com.artisanplatform.common.response.ApiResponse} error
 * envelope without each module reinventing the mapping.
 */
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApplicationException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
