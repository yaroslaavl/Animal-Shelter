package org.shelter.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactNumberValidator implements ConstraintValidator<ContactNumber,String> {

    @Override
    public void initialize(ContactNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String homePhone, ConstraintValidatorContext constraintValidatorContext) {
        if (homePhone == null || homePhone.isEmpty()) {
            return true;
        }
        String formattedNumberOfPhone = homePhone.replaceAll("[^0-9]","");
        Pattern regex = Pattern.compile("\\d{9}");
        Matcher matcher = regex.matcher(formattedNumberOfPhone);
        try{
            if (matcher.matches()) {
                return true;
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;
    }
}
