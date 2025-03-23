package org.shelter.app.exception;

public class UserAccountAlreadyActivatedException extends RuntimeException {
    public UserAccountAlreadyActivatedException(String message) {
        super(message);
    }
}
