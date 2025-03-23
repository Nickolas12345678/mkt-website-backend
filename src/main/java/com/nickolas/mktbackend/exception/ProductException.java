package com.nickolas.mktbackend.exception;

import org.springframework.http.HttpStatus;

public class ProductException extends RuntimeException {
    private final HttpStatus status;

    public ProductException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
