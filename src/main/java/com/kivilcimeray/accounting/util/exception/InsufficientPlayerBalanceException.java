package com.kivilcimeray.accounting.util.exception;

public class InsufficientPlayerBalanceException extends RuntimeException {
    public InsufficientPlayerBalanceException(String s) {
        super(s);
    }
}