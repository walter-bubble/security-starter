package com.walter.toolkit.security_starter.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message () default "Password must be at least 8 characters and include uppercase,lowercase,a number and a special character";
    Class<?>[] group() default {};
    Class<? extends Payload>[] payload ()default {};
}
