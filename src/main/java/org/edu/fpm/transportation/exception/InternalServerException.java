package org.edu.fpm.transportation.exception;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

/**
 * Exception thrown for internal server errors that cannot be handled gracefully.
 */
public class InternalServerException extends ApplicationException {
    public InternalServerException(String message, Throwable cause) {
        super(SC_INTERNAL_SERVER_ERROR, message, cause);
    }
}
