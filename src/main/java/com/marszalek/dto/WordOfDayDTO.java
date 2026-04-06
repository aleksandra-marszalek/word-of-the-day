package com.marszalek.dto;

public record WordOfDayDTO(
        String date,
        String definition,
        String partOfSpeech,
        String phonetic,
        String audioUrl,
        int length
) {
}
