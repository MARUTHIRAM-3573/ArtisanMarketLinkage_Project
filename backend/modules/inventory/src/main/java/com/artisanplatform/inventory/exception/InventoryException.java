package com.artisanplatform.inventory.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the inventory module. Extend with more specific subtypes as business rules are implemented. */
public class InventoryException extends ApplicationException {
    public InventoryException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "INVENTORY_ERROR", message);
    }
}
