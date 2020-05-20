package com.kivilcimeray.accounting.model;


import org.junit.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentTransactionUnitTest {

    @Test
    public void shouldReturnTrueWhenPaymentTransactionIsWithdraw() {
        WithdrawTransaction sampleWithdrawTransaction = WithdrawTransaction.builder().transactionCode(UUID.randomUUID()).build();

        assertTrue(sampleWithdrawTransaction.isWithdraw());
    }

    @Test
    public void shouldReturnFalseWhenPaymentTransactionIsCredit() {
        CreditTransaction sampleCreditTransaction = CreditTransaction.builder().transactionCode(UUID.randomUUID()).build();

        assertFalse(sampleCreditTransaction.isWithdraw());
    }
}