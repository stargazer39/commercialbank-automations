package com.dehemi.combank.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnknownAuthException extends Exception {
    public UnknownAuthException(Exception e) {
        super("unknown auth exception");
        log.error(e.getMessage(), e);
    }
}
