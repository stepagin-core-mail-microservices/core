package ru.stepagin.core.exception;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String id) {
        super("Image with id " + id + " not found");
    }
}
