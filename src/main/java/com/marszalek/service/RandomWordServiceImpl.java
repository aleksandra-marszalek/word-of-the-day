package com.marszalek.service;

import com.marszalek.client.RandomWordApiClient;
import com.marszalek.error.WordFetchException;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Singleton
public class RandomWordServiceImpl implements RandomWordService {

    /** Plain single words only: the game is built around guessing letters. */
    private static final Pattern VALID_WORD = Pattern.compile("^[a-z]{3,20}$");

    private final RandomWordApiClient randomWordApiClient;

    public RandomWordServiceImpl(RandomWordApiClient randomWordApiClient) {
        this.randomWordApiClient = randomWordApiClient;
    }

    @Override
    public String generateRandomWord() {
        String raw;
        try {
            raw = randomWordApiClient.getRandomWord().stream().findFirst()
                    .orElseThrow(() -> new WordFetchException("No random word found"));
        } catch (WordFetchException e) {
            throw e;
        } catch (Exception e) {
            // Wrap everything (HTTP errors, unexpected bodies) so the caller's retry policy applies.
            throw new WordFetchException("Random word request failed: " + e.getMessage(), e);
        }

        var word = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (!VALID_WORD.matcher(word).matches()) {
            log.warn("Rejecting unsuitable random word: '{}'", raw);
            throw new WordFetchException("Random word is not a plain single word");
        }
        return word;
    }
}
