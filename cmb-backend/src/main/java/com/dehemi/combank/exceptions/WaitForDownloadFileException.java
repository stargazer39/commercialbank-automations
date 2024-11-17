package com.dehemi.combank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class WaitForDownloadFileException extends Exception {
    public WaitForDownloadFileException(String message) {
        super(message);
    }
}
