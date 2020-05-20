package com.kivilcimeray.accounting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "payment_transaction")
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRANSACTION_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    UUID transactionCode;

    BigDecimal amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    Player player;

    @CreatedDate
    Date createDate;

    @PrePersist
    protected void onCreate() {
        createDate = new Date();
    }

    public abstract TransactionType getTransactionType();

    public boolean isWithdraw() {
        return getTransactionType().equals(TransactionType.WITHDRAW);
    }
}