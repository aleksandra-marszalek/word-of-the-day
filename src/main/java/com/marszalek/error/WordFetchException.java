package com.marszalek.error;

public class WordFetchException extends IllegalStateException {
    public WordFetchException(String message) {
        super(message);
    }

    public WordFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
