package com.artisanplatform.pricing.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the pricing module. Extend with more specific subtypes as business rules are implemented. */
public class SkuPriceException extends ApplicationException {
    public SkuPriceException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "PRICING_ERROR", message);
    }
}
