package com.marszalek.client;

import com.marszalek.dto.WiktionaryEntryDTO;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

import java.util.List;
import java.util.Map;

/**
 * Wiktionary REST API (no key required). The response is keyed by language code, e.g. "en".
 * Wikimedia asks API clients to send a descriptive User-Agent.
 */
@Client("https://en.wiktionary.org")
@Header(name = "User-Agent", value = "word-of-the-day/0.1 (https://wotd.aleksandramarszalek.co.uk)")
public interface WiktionaryApiClient {

    @Get("/api/rest_v1/page/definition/{word}")
    Map<String, List<WiktionaryEntryDTO>> getDefinition(@PathVariable String word);
}
