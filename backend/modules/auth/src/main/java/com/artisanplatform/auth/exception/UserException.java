package com.artisanplatform.auth.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the auth module. Extend with more specific subtypes as business rules are implemented. */
public class UserException extends ApplicationException {
    public UserException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTH_ERROR", message);
    }
}
