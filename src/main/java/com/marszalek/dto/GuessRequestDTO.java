package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record GuessRequestDTO(
        @NotBlank String guess
) {
}