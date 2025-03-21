package org.shelter.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;


public class ImageFileValidator implements ConstraintValidator<ImageValid, MultipartFile> {
    private long maxSize;

    @Override
    public void initialize(ImageValid constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile == null) {
            return true;
        }

        String contentType = multipartFile.getContentType();
        boolean validType = contentType != null && (contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg"));

        boolean validSize = multipartFile.getSize() <= maxSize;

        return validType && validSize;
    }
}
