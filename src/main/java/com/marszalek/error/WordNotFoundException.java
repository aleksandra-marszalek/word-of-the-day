package com.marszalek.error;

public class WordNotFoundException extends IllegalStateException {
    public WordNotFoundException(String message) {
        super(message);
    }
}
