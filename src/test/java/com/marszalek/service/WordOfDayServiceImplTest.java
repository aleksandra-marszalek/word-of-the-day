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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void getWordOfDay_returnsWordOfDay_whenWordFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.of(wordOfDay));

        var word = wordOfDayService.getWordOfDay();

        assertEquals(wordOfDay.getDefinition(), word.definition());
        assertEquals(11, word.length());
    }

    @Test
    void getWordOfDay_returnsRecentWordOfDay_whenWordNotFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.of(wordOfDay));

        var word = wordOfDayService.getWordOfDay();
        assertEquals(wordOfDay.getDefinition(), word.definition());
    }

    @Test
    void getWordOfDay_throwsWordNotFoundException_whenRecentWordNotFound() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.empty());

        assertThrows(WordNotFoundException.class, () -> wordOfDayService.getWordOfDay());
    }

    @Test
    void getWordOfDay_throwsWordNotFoundException_whenWordReturnedIncomplete() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.of(new WordOfDay()));

        assertThrows(WordNotFoundException.class, () -> wordOfDayService.getWordOfDay());
    }

    @Test
    void getWordOfDay_throwsWordNotFoundException_whenMostRecentWordReturnedIncomplete() {
        when(wordRepository.findByDate(TODAY)).thenReturn(Optional.empty());
        when(wordRepository.findMostRecent()).thenReturn(Optional.of(new WordOfDay()));

        assertThrows(WordNotFoundException.class, () -> wordOfDayService.getWordOfDay());
    }
}