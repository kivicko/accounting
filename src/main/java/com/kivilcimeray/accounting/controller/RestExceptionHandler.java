package com.kivilcimeray.accounting.controller;

import com.kivilcimeray.accounting.util.exception.ApiError;
import com.kivilcimeray.accounting.util.exception.InsufficientPlayerBalanceException;
import com.kivilcimeray.accounting.util.exception.PlayerNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            PlayerNotFoundException ex) {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InsufficientPlayerBalanceException.class)
    protected ResponseEntity<Object> handleBalanceNotEnough(
            InsufficientPlayerBalanceException ex) {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_ACCEPTABLE)
                .message(ex.getMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<Object> handlePessimisticLockEx(PessimisticLockingFailureException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .message(HttpStatus.TOO_MANY_REQUESTS.toString())
                .build();

        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getFieldError().getDefaultMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .message(ex.getBindingResult().getFieldError().getDefaultMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .message(ex.getLocalizedMessage())
                .build();

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        apiError.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}