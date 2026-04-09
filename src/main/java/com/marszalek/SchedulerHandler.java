package com.marszalek;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.marszalek.service.WordOfDayService;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerHandler extends MicronautRequestHandler<ScheduledEvent, Void> {

    @Inject
    WordOfDayService wordOfDayService;

    @Override
    public Void execute(ScheduledEvent input) {
        log.info("Scheduler triggered by EventBridge");
        var word = wordOfDayService.fetchAndStoreWordOfDay();
        log.info("Word of the day saved: {}", word.getWord());
        return null;
    }
}