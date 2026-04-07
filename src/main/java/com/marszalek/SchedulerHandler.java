package com.marszalek;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.marszalek.service.WordOfDayService;
import io.micronaut.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SchedulerHandler implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final WordOfDayService wordOfDayService;

    public SchedulerHandler() {
        var context = ApplicationContext.run();
        this.wordOfDayService = context.getBean(WordOfDayService.class);
    }

    @Override
    public Map<String, String> handleRequest(Map<String, Object> event, Context context) {
        log.info("Scheduler triggered by EventBridge");
        var word = wordOfDayService.fetchAndStoreWordOfDay();
        log.info("Word of the day saved: {}", word.getWord());
        return Map.of("message", "Word saved: " + word.getWord());
    }
}