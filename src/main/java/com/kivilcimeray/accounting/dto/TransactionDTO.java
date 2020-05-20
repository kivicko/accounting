package com.kivilcimeray.accounting.dto;

import com.kivilcimeray.accounting.util.validation.UniqueCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    @NotNull(message = "The playerID should be specified.")
    public Long playerID;

    @NotNull(message = "The amount should be specified.")
    @Positive(message = "The amount should be greater than zero.")
    public BigDecimal amount;

    @NotNull(message = "The transactionCode must be specified.")
    @UniqueCode(message = "The transactionCode must be unique.")
    public UUID transactionCode;
}