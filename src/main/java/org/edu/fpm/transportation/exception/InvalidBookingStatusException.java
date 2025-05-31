package org.edu.fpm.transportation.exception;

import static org.apache.http.HttpStatus.SC_NOT_ACCEPTABLE;

public class InvalidBookingStatusException extends ApplicationException {
    public InvalidBookingStatusException(String message) {
        super(SC_NOT_ACCEPTABLE, message);
    }
}
