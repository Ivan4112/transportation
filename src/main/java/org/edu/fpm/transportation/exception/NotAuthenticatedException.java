package org.edu.fpm.transportation.exception;

import org.apache.http.HttpStatus;

public class NotAuthenticatedException extends ApplicationException{

    public NotAuthenticatedException(String message) {
        super(HttpStatus.SC_UNAUTHORIZED, message);
    }
}
