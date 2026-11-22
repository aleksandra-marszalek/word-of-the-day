package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record WiktionaryDefinitionDTO(String definition) {
}
