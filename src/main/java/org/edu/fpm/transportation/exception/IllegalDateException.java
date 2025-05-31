package org.edu.fpm.transportation.exception;

import org.apache.http.HttpStatus;

public class IllegalDateException extends ApplicationException{

    public IllegalDateException(String message) {
        super(HttpStatus.SC_BAD_REQUEST, message);
    }
}
