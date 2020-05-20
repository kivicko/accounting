package com.kivilcimeray.accounting.controller;

import com.kivilcimeray.accounting.util.exception.ApiError;
import com.kivilcimeray.accounting.util.exception.InsufficientPlayerBalanceException;
import com.kivilcimeray.accounting.util.exception.PlayerNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestExceptionHandlerUnitTest {

    @InjectMocks
    private RestExceptionHandler handler;

    @Test
    public void shouldHandlePlayerNotFoundExceptionAndReturnResponse() {
        String sampleMessage = "some sample messages";
        PlayerNotFoundException ex = new PlayerNotFoundException(sampleMessage);

        ResponseEntity<Object> responseEntity = handler.handleEntityNotFound(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiError body = (ApiError) responseEntity.getBody();

        assertNotNull(body);
        assertEquals(sampleMessage, body.getMessage());
    }

    @Test
    public void shouldHandleInsufficientPlayerBalanceExceptionAndReturnResponse() {
        String sampleMessage = "some sample messages";
        InsufficientPlayerBalanceException ex = new InsufficientPlayerBalanceException(sampleMessage);

        ResponseEntity<Object> responseEntity = handler.handleBalanceNotEnough(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());

        ApiError body = (ApiError) responseEntity.getBody();

        assertNotNull(body);
        assertEquals(sampleMessage, body.getMessage());
    }

    @Test
    public void shouldHandlePessimisticLockExceptionAndReturnResponse() {
        PessimisticLockingFailureException ex = new PessimisticLockingFailureException("");

        ResponseEntity<Object> responseEntity = handler.handlePessimisticLockEx(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        ApiError body = (ApiError) responseEntity.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.toString(), body.getMessage());
    }

    @Test
    public void shouldHandleBindExceptionAndReturnResponse() {
        BindException ex = Mockito.mock(BindException.class);
        String defaultMessage = "Default message";
        FieldError fielderror = new FieldError("objectName", "field", defaultMessage);

        when(ex.getFieldError()).thenReturn(fielderror);

        ResponseEntity<Object> responseEntity = handler.handleBindException(ex, null, null, null);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ApiError body = (ApiError) responseEntity.getBody();

        assertNotNull(body);
        assertEquals(defaultMessage, body.getMessage());
    }
}