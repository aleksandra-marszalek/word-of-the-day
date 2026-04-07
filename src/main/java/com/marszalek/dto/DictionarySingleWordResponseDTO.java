package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record DictionarySingleWordResponseDTO(
        String word,
        List<PhoneticDTO> phonetics,
        List<MeaningDTO> meanings
) {
}
