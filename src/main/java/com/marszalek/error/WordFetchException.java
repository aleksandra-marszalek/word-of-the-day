package com.marszalek.error;

public class WordFetchException extends IllegalStateException {
    public WordFetchException(String message) {
        super(message);
    }
}
