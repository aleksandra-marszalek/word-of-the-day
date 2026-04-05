package com.marszalek.dto;

import java.util.List;

public record MeaningDTO(String partOfSpeech, List<DefinitionDTO> definitions, List<String> synonyms, List<String> antonyms) {
}
