package com.artisanplatform.b2b.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the b2b module. Extend with more specific subtypes as business rules are implemented. */
public class B2bInquiryException extends ApplicationException {
    public B2bInquiryException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "B2B_ERROR", message);
    }
}
