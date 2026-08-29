package com.artisanplatform.seller.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the seller module. Extend with more specific subtypes as business rules are implemented. */
public class SellerException extends ApplicationException {
    public SellerException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "SELLER_ERROR", message);
    }
}
