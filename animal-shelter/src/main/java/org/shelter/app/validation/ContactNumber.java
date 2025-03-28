package org.shelter.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = ContactNumberValidator.class)
public @interface ContactNumber {

    String message() default "Invalid contact number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}