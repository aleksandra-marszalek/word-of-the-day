package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RevealDTO(String word) {
}
