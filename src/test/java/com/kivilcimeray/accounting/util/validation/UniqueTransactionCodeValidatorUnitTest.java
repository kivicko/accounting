package com.kivilcimeray.accounting.util.validation;

import com.kivilcimeray.accounting.repository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UniqueTransactionCodeValidatorUnitTest {

    @InjectMocks
    private UniqueTransactionCodeValidator validator;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void shouldReturnFalseWhenPaymentTransactionExistOnDB() {
        UUID sampleUUID = UUID.randomUUID();

        when(transactionRepository.existsPaymentTransactionByTransactionCode(sampleUUID)).thenReturn(true);

        boolean response = validator.isValid(sampleUUID, null);

        assertFalse(response);
        verify(transactionRepository).existsPaymentTransactionByTransactionCode(sampleUUID);
    }

    @Test
    public void shouldReturnTrueWhenPaymentTransactionNumberIsUnique() {
        UUID sampleUUID = UUID.randomUUID();

        when(transactionRepository.existsPaymentTransactionByTransactionCode(sampleUUID)).thenReturn(false);

        boolean response = validator.isValid(sampleUUID, null);

        assertTrue(response);
        verify(transactionRepository).existsPaymentTransactionByTransactionCode(sampleUUID);
    }

}