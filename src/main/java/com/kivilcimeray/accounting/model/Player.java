package com.kivilcimeray.accounting.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "player")
public class Player {

    @Id
    private Long id;

    private BigDecimal balance;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<PaymentTransaction> transactionHistory;

    public Player(Long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public void updateBalance(BigDecimal amount, TransactionType transactionType) {
        if (TransactionType.CREDIT.equals(transactionType)) {
            this.addBalance(amount);
        } else {
            this.subtractBalance(amount);
        }
    }

    private void addBalance(BigDecimal addition) {
        this.balance = balance.add(addition);
    }

    private void subtractBalance(BigDecimal deduction) {
        this.balance = balance.subtract(deduction);
    }

    public void addTransactionHistory(PaymentTransaction transaction) {
        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }

        transactionHistory.add(transaction);
    }
}