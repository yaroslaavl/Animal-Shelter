package org.shelter.app.exception;

public class PetAlreadyRegisteredException extends RuntimeException {
    public PetAlreadyRegisteredException(String message) {
        super(message);
    }
}
