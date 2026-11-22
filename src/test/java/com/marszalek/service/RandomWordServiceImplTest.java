package com.marszalek.service;

import com.marszalek.client.RandomWordApiClient;
import com.marszalek.error.WordFetchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomWordServiceImplTest {

    @InjectMocks
    private RandomWordServiceImpl service;

    @Mock
    private RandomWordApiClient client;

    @Test
    void returnsNormalisedWord() {
        when(client.getRandomWord()).thenReturn(List.of("  Kinship "));
        assertEquals("kinship", service.generateRandomWord());
    }

    @Test
    void throwsWordFetchException_whenEmptyResponse() {
        when(client.getRandomWord()).thenReturn(List.of());
        assertThrows(WordFetchException.class, () -> service.generateRandomWord());
    }

    @Test
    void wrapsClientFailuresSoRetryApplies() {
        when(client.getRandomWord()).thenThrow(new RuntimeException("HTTP 429"));
        assertThrows(WordFetchException.class, () -> service.generateRandomWord());
    }

    @Test
    void rejectsMultiWordOrNonAlphabeticTokens() {
        when(client.getRandomWord()).thenReturn(List.of("ice cream"));
        assertThrows(WordFetchException.class, () -> service.generateRandomWord());

        when(client.getRandomWord()).thenReturn(List.of("x-ray"));
        assertThrows(WordFetchException.class, () -> service.generateRandomWord());
    }
}
