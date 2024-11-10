package com.dehemi.combank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenInvalidException extends Exception {
    public TokenInvalidException() {
        super("token is invalid");
    }
}
