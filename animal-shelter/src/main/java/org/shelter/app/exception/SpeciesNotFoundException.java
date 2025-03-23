package org.shelter.app.exception;

public class SpeciesNotFoundException extends RuntimeException {
    public SpeciesNotFoundException(String message) {
        super(message);
    }
}
