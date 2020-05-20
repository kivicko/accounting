package com.kivilcimeray.accounting.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlayerDTO {
    private long playerID;
    private BigDecimal currentBalance;
}