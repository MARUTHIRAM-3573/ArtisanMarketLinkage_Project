package com.artisanplatform.catalog.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the catalog module. Extend with more specific subtypes as business rules are implemented. */
public class ProductException extends ApplicationException {
    public ProductException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CATALOG_ERROR", message);
    }
}
