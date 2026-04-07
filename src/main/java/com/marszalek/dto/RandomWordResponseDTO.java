package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RandomWordResponseDTO(
        String word,
        String definition,
        String pronunciation
) {
}
