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
@DiscriminatorValue("CREDIT")
public class CreditTransaction extends PaymentTransaction {

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.CREDIT;
    }

    @Builder
    public CreditTransaction(BigDecimal amount, UUID transactionCode, Player player) {
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.player = player;
    }
}