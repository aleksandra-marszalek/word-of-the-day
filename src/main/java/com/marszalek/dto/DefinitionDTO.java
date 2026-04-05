package com.marszalek.dto;

import java.util.List;

public record DefinitionDTO(String definition, List<String> synonyms, List<String> antonyms) {
}
