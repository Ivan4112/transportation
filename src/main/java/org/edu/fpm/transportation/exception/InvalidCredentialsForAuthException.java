package org.edu.fpm.transportation.exception;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

public class InvalidCredentialsForAuthException extends ApplicationException {
    public InvalidCredentialsForAuthException(String message) {
        super(SC_BAD_REQUEST, message);
    }
}
