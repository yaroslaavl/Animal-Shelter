package org.shelter.app.exception;

public class AdoptionRequestInProgressException extends RuntimeException {
    public AdoptionRequestInProgressException(String message) {
        super(message);
    }
}
