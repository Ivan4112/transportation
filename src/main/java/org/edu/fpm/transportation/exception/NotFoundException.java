package org.edu.fpm.transportation.exception;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;

/**
 * Exception thrown when a requested resource is not found.
 */
public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(SC_NOT_FOUND, message);
    }
}
