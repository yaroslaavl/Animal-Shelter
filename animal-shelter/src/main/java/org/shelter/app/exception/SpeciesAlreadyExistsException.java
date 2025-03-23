package org.shelter.app.exception;

public class SpeciesAlreadyExistsException extends RuntimeException {
    public SpeciesAlreadyExistsException(String message) {
        super(message);
    }
}
