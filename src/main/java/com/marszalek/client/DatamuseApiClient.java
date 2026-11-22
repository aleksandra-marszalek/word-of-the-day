package com.marszalek.client;

import com.marszalek.dto.DatamuseWordDTO;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

import java.util.List;

/**
 * Datamuse API (no key required). Used as a fallback source of definitions.
 */
@Client("https://api.datamuse.com")
@Header(name = "User-Agent", value = "word-of-the-day/0.1 (https://wotd.aleksandramarszalek.co.uk)")
public interface DatamuseApiClient {

    /** {@code md=d} asks for definitions; {@code sp} is an exact spelling match for plain letters. */
    @Get("/words")
    List<DatamuseWordDTO> lookup(@QueryValue("sp") String word,
                                 @QueryValue("md") String metadata,
                                 @QueryValue("max") int max);
}
