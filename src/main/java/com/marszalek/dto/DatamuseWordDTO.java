package com.marszalek.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record DatamuseWordDTO(
        String word,
        List<String> tags,
        List<String> defs
) {
}
