package com.artisanplatform.payment.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the payment module. Extend with more specific subtypes as business rules are implemented. */
public class PaymentException extends ApplicationException {
    public PaymentException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "PAYMENT_ERROR", message);
    }
}
