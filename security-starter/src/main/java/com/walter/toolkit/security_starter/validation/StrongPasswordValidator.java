package com.walter.toolkit.security_starter.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword,String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_-])[A-Za-z\\d@$!%*?&.#_-]{8,}$");
    @Override
    public boolean isValid(String password, ConstraintValidatorContext validatorContext){
        if(password == null || password.isBlank()){
            return true;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
