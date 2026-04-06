package com.marszalek.error;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ErrorResponse(
        String message
) {
}
