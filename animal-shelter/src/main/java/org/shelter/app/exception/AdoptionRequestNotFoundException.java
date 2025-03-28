package org.shelter.app.exception;

public class AdoptionRequestNotFoundException extends RuntimeException {
    public AdoptionRequestNotFoundException(String message) {
        super(message);
    }
}
