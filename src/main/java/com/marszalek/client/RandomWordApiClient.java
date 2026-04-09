package com.marszalek.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;

import java.util.List;
import java.util.Optional;

@Client("https://api.api-ninjas.com")
public interface RandomWordApiClient {

    @Get("/v2/randomword")
    @Header(name = "X-Api-Key", value = "${api.ninjas.key}")
    List<String> getRandomWord();
}
