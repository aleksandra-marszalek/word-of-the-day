package com.marszalek.dto;

import java.util.List;

public record DictionarySingleWordResponseDTO(
        String word,
        List<PhoneticDTO> phonetics,
        List<MeaningDTO> meanings
) {
}
