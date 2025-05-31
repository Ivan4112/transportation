package org.edu.fpm.transportation.exception;

import org.apache.http.HttpStatus;

public class ExecutionException extends ApplicationException {
    public ExecutionException(String message) {
        super(HttpStatus.SC_EXPECTATION_FAILED, message);
    }
}
