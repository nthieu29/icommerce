package com.nthieu.productservice.controller;

import com.nthieu.productservice.model.ErrorDetail;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException exception, WebRequest request) {
        ErrorDetail errorDetails = new ErrorDetail(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NO_CONTENT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetail errorDetails = new ErrorDetail(LocalDateTime.now(), "Validation Failed",
                ex.getBindingResult().toString());
        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
