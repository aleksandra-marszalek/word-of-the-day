package com.marszalek.scheduler;

import com.marszalek.error.DefinitionFetchException;
import com.marszalek.error.WordFetchException;
import com.marszalek.service.WordOfDayService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class WordOfDayScheduler {

    private final WordOfDayService wordOfDayService;

    public WordOfDayScheduler(WordOfDayService wordOfDayService) {
        this.wordOfDayService = wordOfDayService;
    }

    @Scheduled(cron = "0 9 * * *")
    public void scheduleWordOfDay() {
        try {
            var savedWord = wordOfDayService.fetchAndStoreWordOfDay();
            log.info("Word of day saved: {}", savedWord.getWord());
        } catch (DefinitionFetchException | WordFetchException e) {
            log.error("Word of the day was not processed properly due to internal errors: ", e);
        } catch (Exception e) {
            log.error("Word of the day was not processed properly due to unexpected errors: ", e);
        }
    }
}
