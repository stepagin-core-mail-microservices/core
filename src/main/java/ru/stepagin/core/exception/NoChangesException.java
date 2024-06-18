package ru.stepagin.core.exception;

public class NoChangesException extends RuntimeException {
    public NoChangesException(String message) {
        super(message);
    }

    public NoChangesException() {
        super("No changes were made");
    }
}
