package com.kivilcimeray.accounting.util;

import java.math.BigDecimal;

public class AccountingUtils {

    public static BigDecimal $(Integer amount) {
        return new BigDecimal(amount);
    }
}