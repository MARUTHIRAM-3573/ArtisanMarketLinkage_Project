package com.artisanplatform.ai.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the ai module. Extend with more specific subtypes as business rules are implemented. */
public class AiJobException extends ApplicationException {
    public AiJobException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AI_ERROR", message);
    }
}
