package com.artisanplatform.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown whenever an authenticated caller attempts to read or write a
 * resource they do not own (IDOR prevention — every owner-scoped endpoint
 * across every module must check this before acting, per
 * docs/architecture/08_SECURITY_AND_VAULT.md Part A.5). Maps to HTTP 403.
 */
public class OwnershipViolationException extends ApplicationException {
    public OwnershipViolationException(String resourceName) {
        super(HttpStatus.FORBIDDEN, "OWNERSHIP_VIOLATION",
                "You do not have permission to access this " + resourceName);
    }
}
