package com.marszalek.service;

import com.marszalek.error.WordNotFoundException;
import com.marszalek.model.WordOfDay;
import com.marszalek.repository.WordRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordOfDayServiceImplTest {

    @InjectMocks
    private WordOfDayServiceImpl wordOfDayService;

    @Mock
    private WordRepository wordRepository;

    @Mock
    private RandomWordService randomWordService;

    @Mock
    private DictionaryService dictionaryService;

    private static String TODAY;
    private static WordOfDay wordOfDay;

    @BeforeAll
    static void setUp() {
        TODAY = LocalDate.now().toString();
        wordOfDay = new WordOfDay();
        wordOfDay.setDate(TODAY);
        wordOfDay.setWord("caterpillar");
        wordOfDay.setDefinition("The larva of a butterfly or moth; leafworm.");
        wordOfDay.setPhonetic("/ˈkætəˌpɪlə/");
        wordOfDay.setAudioUrl("https://api.dictionaryapi.dev/media/pronunciations/en/caterpillar-us.mp3");
        wordOfDay.setPartOfSpeech("noun");
        wordOfDay.setPk("WORD");
        wordOfDay.setFetchedAt(Instant.now().toString());

    }

    @Test
    void getWordOfDay_returnsWordOfDay_whenValidWordFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.of(wordOfDay));

        var word = wordOfDayService.getValidWordOfDay();

        assertEquals(wordOfDay.getDefinition(), word.definition());
        assertEquals(11, word.length());
    }

    @Test
    void getWordOfDay_returnsRecentWordOfDay_whenValidWordNotFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.of(wordOfDay));

        var word = wordOfDayService.getValidWordOfDay();
        assertEquals(wordOfDay.getDefinition(), word.definition());
    }

    @Test
    void getWordOfDay_throwsWordNotFoundException_whenRecentValidWordNotFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.empty());

        assertThrows(WordNotFoundException.class, () -> wordOfDayService.getValidWordOfDay());
    }

    @Test
    void getWordOfDay_throwsWordNotFoundException_whenValidWordReturnedIncomplete() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.of(new WordOfDay()));

        assertThrows(WordNotFoundException.class, () -> wordOfDayService.getValidWordOfDay());
    }

    @Test
    void getWordOfDay_throwsWordNotFoundException_whenMostRecentValidWordReturnedIncomplete() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.of(new WordOfDay()));

        assertThrows(WordNotFoundException.class, () -> wordOfDayService.getValidWordOfDay());
    }

    @Test
    void checkGuess_returnsTrue_whenWordFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.of(wordOfDay));

        assertTrue(wordOfDayService.checkGuess("caterpillar"));
    }

    @Test
    void checkGuess_returnsFalse_whenWordNotFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.of(wordOfDay));

        assertFalse(wordOfDayService.checkGuess("butterfly"));
    }

    @Test
    void checkGuess_throwsWordNotFoundException_whenNoWordAvailable() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.empty());
        assertThrows(WordNotFoundException.class, () -> wordOfDayService.checkGuess("caterpillar"));
    }
}