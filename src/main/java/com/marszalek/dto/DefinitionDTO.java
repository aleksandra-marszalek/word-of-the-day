package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record DefinitionDTO(
        String definition,
        List<String> synonyms,
        List<String> antonyms
) {
}
