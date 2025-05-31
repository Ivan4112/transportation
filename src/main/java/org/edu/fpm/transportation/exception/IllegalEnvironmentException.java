package org.edu.fpm.transportation.exception;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class IllegalEnvironmentException extends ApplicationException {
    public IllegalEnvironmentException(String message) {
        super(SC_NOT_FOUND, message);
    }
}
