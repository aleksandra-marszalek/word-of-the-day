package com.marszalek.error;

public class DefinitionFetchException extends IllegalStateException {
    public DefinitionFetchException(String message) {
        super(message);
    }

    public DefinitionFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
