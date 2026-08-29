package com.artisanplatform.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown by any module's service layer when a requested entity does not exist. Maps to HTTP 404. */
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND",
                resourceName + " not found: " + identifier);
    }
}
