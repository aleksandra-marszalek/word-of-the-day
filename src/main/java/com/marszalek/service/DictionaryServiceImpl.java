package com.marszalek.service;

import com.marszalek.client.DictionaryApiClient;
import com.marszalek.dto.PhoneticDTO;
import com.marszalek.error.DefinitionFetchException;
import com.marszalek.model.DictionaryData;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Slf4j
@Singleton
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryApiClient dictionaryApiClient;

    public DictionaryServiceImpl(DictionaryApiClient dictionaryApiClient) {
        this.dictionaryApiClient = dictionaryApiClient;
    }

    @Override
    public DictionaryData getWordDefinitionData(String word) {
        var response = dictionaryApiClient.getDefinition(word).stream()
                .findFirst()
                .orElseThrow(() -> new DefinitionFetchException(format("Invalid definition response for word: %s", word)));

        var meanings = response.meanings().stream()
                .findFirst()
                .orElseThrow(() -> new DefinitionFetchException(format("Invalid meaning response for word: %s", word)));

        var definition = meanings.definitions().stream()
                .findFirst()
                .orElseThrow(() -> new DefinitionFetchException(format("Invalid definition for word: %s", word)));


        var phoneticsText = response.phonetics().stream()
                .filter(p -> p.text() != null && !p.text().isBlank())
                .findFirst()
                .map(PhoneticDTO::text)
                .orElse("");

        var audioUrl = response.phonetics().stream()
                .filter(p -> p.audio() != null && !p.audio().isBlank())
                .findFirst()
                .map(PhoneticDTO::audio)
                .orElse("");

        return new DictionaryData(definition.definition(), meanings.partOfSpeech(), phoneticsText, audioUrl);
    }
}
