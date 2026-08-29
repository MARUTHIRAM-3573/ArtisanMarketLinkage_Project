package com.artisanplatform.commerce.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the commerce module. Extend with more specific subtypes as business rules are implemented. */
public class OrderException extends ApplicationException {
    public OrderException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "COMMERCE_ERROR", message);
    }
}
