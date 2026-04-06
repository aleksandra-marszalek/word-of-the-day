package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record WordOfDayDTO(
        String date,
        String definition,
        String partOfSpeech,
        String phonetic,
        String audioUrl,
        int length
) {
}
