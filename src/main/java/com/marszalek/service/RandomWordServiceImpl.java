package com.marszalek.service;

import com.marszalek.client.RandomWordApiClient;
import com.marszalek.error.WordFetchException;
import jakarta.inject.Singleton;

@Singleton
public class RandomWordServiceImpl implements RandomWordService {

    private final RandomWordApiClient randomWordApiClient;

    public RandomWordServiceImpl(RandomWordApiClient randomWordApiClient) {
        this.randomWordApiClient = randomWordApiClient;
    }

    @Override
    public String generateRandomWord() {
        return randomWordApiClient.getRandomWord()
                .orElseThrow(() -> new WordFetchException("No random word found"))
                .word();
    }
}