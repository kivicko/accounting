package com.kivilcimeray.accounting.util.validation;

import com.kivilcimeray.accounting.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class UniqueTransactionCodeValidator implements ConstraintValidator<UniqueCode, UUID> {

    private final TransactionRepository transactionRepository;

    public UniqueTransactionCodeValidator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        return !transactionRepository.existsPaymentTransactionByTransactionCode(value);
    }
}