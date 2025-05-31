package org.edu.fpm.transportation.exception;

import lombok.Getter;
import org.apache.http.HttpStatus;

@Getter
public class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(HttpStatus.SC_CONFLICT, message);
    }
}
