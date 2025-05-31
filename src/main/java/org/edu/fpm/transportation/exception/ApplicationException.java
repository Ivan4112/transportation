package org.edu.fpm.transportation.exception;

import lombok.Getter;

/**
 * Base exception for application-specific errors.
 */
@Getter
public class ApplicationException extends RuntimeException {
    private final int statusCode;

    public ApplicationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApplicationException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
