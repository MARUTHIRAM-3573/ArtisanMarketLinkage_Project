package com.artisanplatform.media.exception;

import com.artisanplatform.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/** Domain-specific exception for the media module. Extend with more specific subtypes as business rules are implemented. */
public class MediaAssetException extends ApplicationException {
    public MediaAssetException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "MEDIA_ERROR", message);
    }
}
