package com.book.library.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {

    String message() default
            """
            Invalid password. Please recheck the password, whether it contains
            - at least 12 characters,
            - at least 1 number,
            - at least 1 special character,
            - at least 1 uppercase letter,
            - at least 1 lowercase letter,
            - no blanks""";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
