package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record MeaningDTO(
        String partOfSpeech,
        List<DefinitionDTO> definitions,
        List<String> synonyms,
        List<String> antonyms
) {
}
