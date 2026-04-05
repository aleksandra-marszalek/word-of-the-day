package com.marszalek.client;

import com.marszalek.dto.DictionarySingleWordResponseDTO;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

import java.util.List;

@Client(value = "https://api.dictionaryapi.dev")
public interface DictionaryApiClient {

    @Get(value = "/api/v2/entries/en/{word}")
    List<DictionarySingleWordResponseDTO> getDefinition(@PathVariable String word);
}
