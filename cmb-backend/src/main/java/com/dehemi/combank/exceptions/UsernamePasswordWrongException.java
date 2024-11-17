package com.dehemi.combank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UsernamePasswordWrongException extends Exception {
    public UsernamePasswordWrongException() {
        super("username or password is incorrect");
    }
}