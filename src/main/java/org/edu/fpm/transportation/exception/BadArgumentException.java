package org.edu.fpm.transportation.exception;

import org.apache.http.HttpStatus;

public class BadArgumentException extends ApplicationException{

    public BadArgumentException(String message) {
        super(HttpStatus.SC_BAD_REQUEST, message);
    }
}
