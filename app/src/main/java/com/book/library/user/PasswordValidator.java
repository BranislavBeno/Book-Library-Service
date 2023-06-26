package com.book.library.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() < 12) {
            return false;
        }

        Pattern pattern = Pattern.compile("[a-zA-Z\\d\\W]+");
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }
}
