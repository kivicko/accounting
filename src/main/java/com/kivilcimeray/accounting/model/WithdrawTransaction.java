package com.kivilcimeray.accounting.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue(value = "WITHDRAW")
public class WithdrawTransaction extends PaymentTransaction {

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAW;
    }

    @Builder
    public WithdrawTransaction(BigDecimal amount, UUID transactionCode, Player player) {
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.player = player;
    }
}