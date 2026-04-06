package com.marszalek.service;

import com.marszalek.dto.WordOfDayDTO;
import com.marszalek.error.DefinitionFetchException;
import com.marszalek.error.WordFetchException;
import com.marszalek.error.WordNotFoundException;
import com.marszalek.model.WordOfDay;
import com.marszalek.repository.WordRepository;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;

import static software.amazon.awssdk.utils.StringUtils.isBlank;
import static software.amazon.awssdk.utils.StringUtils.isNotBlank;

@Slf4j
@Singleton
public class WordOfDayServiceImpl implements WordOfDayService {

    private final RandomWordService randomWordService;
    private final DictionaryService dictionaryService;
    private final WordRepository wordRepository;

    public WordOfDayServiceImpl(RandomWordService randomWordService, DictionaryService dictionaryService, WordRepository wordRepository) {
        this.randomWordService = randomWordService;
        this.dictionaryService = dictionaryService;
        this.wordRepository = wordRepository;
    }

    @Retryable(attempts = "5", delay = "5s")
    @Override
    public WordOfDay fetchAndStoreWordOfDay() {
        String randomWord = randomWordService.generateRandomWord();
        if (isBlank(randomWord)) {
            log.error("Invalid Random Word received");
            throw new WordFetchException("Valid Random Word Not Returned from Random Word Service");
        }

        var data = dictionaryService.getWordDefinitionData(randomWord);
        if (data == null || isBlank(data.definition())) {
            log.error("Invalid Word Definition for word {}", randomWord);
            throw new DefinitionFetchException("Invalid Word Definition received from Dictionary Service");
        }

        WordOfDay wordOfDay = new WordOfDay();
        wordOfDay.setWord(randomWord);
        wordOfDay.setDefinition(data.definition());
        wordOfDay.setDate(LocalDate.now().toString());
        wordOfDay.setFetchedAt(Instant.now().toString());
        if (isNotBlank(data.partOfSpeech())) {
            wordOfDay.setPartOfSpeech(data.partOfSpeech());
        }
        if (isNotBlank(data.phonetic())) {
            wordOfDay.setPhonetic(data.phonetic());
        }
        if (isNotBlank(data.audioUrl())) {
            wordOfDay.setAudioUrl(data.audioUrl());
        }

        wordRepository.save(wordOfDay);

        log.info("Word Of Day Saved successfully: {}", wordOfDay.getWord());
        return wordOfDay;
    }

    @Override
    public WordOfDayDTO getValidWordOfDay() {
        var word = getWordOfDay();

        return new WordOfDayDTO(
                word.getDate(),
                word.getDefinition(),
                word.getPartOfSpeech(),
                word.getPhonetic(),
                word.getAudioUrl(),
                word.getWord().length());
    }

    @Override
    public boolean checkGuess(String guess) {
        var word = getWordOfDay();
        return word.getWord().equalsIgnoreCase(guess);
    }

    private WordOfDay getWordOfDay() {
        var word = wordRepository.findByDate(LocalDate.now().toString())
                .orElseGet(() -> wordRepository.findMostRecent()
                        .orElseThrow(() -> new WordNotFoundException("No word of the day available")));

        if (isBlank(word.getWord()) || isBlank(word.getDefinition())) {
            throw new WordNotFoundException("Malformed word of the day received");
        }
        return word;
    }
}
