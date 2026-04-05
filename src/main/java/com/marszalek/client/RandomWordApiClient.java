package com.marszalek.client;

import com.marszalek.dto.RandomWordResponseDTO;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

import java.util.Optional;

@Client(value = "https://random-words-api.vercel.app")
public interface RandomWordApiClient {

    @Get(value = "/word")
    Optional<RandomWordResponseDTO> getRandomWord();
}
