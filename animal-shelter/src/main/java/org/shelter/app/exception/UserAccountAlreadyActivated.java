package org.shelter.app.exception;

public class UserAccountAlreadyActivated extends RuntimeException {
    public UserAccountAlreadyActivated(String message) {
        super(message);
    }
}
