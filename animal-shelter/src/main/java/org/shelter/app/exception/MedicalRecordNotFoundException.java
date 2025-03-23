package org.shelter.app.exception;

public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }
}
