package com.marszalek.dto;

import jakarta.validation.constraints.NotBlank;

public record GuessRequestDTO(
        @NotBlank String guess
) {
}