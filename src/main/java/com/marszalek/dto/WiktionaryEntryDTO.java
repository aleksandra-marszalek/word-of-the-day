package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record WiktionaryEntryDTO(
        String partOfSpeech,
        String language,
        List<WiktionaryDefinitionDTO> definitions
) {
}
