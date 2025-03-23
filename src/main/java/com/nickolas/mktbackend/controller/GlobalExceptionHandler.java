package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.exception.ProductException;
import com.nickolas.mktbackend.exception.SellerException;
import com.nickolas.mktbackend.exception.UserException;
import com.nickolas.mktbackend.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse> handleUserException(UserException e) {
        ApiResponse response = new ApiResponse();
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(SellerException.class)
    public ResponseEntity<ApiResponse> handleSellerException(SellerException ex) {
        ApiResponse response = new ApiResponse();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> handleProductException(ProductException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}
