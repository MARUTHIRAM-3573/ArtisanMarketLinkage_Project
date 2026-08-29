package com.artisanplatform.market.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the market module. Extend with more specific subtypes as business rules are implemented. */
public class MarketListingException extends ApplicationException {
    public MarketListingException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "MARKET_ERROR", message);
    }
}
