package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PhoneticDTO(
        String text,
        String audio
) {
}
