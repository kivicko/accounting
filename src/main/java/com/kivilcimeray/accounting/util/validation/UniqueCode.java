package com.kivilcimeray.accounting.util.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueTransactionCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCode {
    String message() default "Invalid uniqueCode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}