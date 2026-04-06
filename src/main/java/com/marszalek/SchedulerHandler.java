package com.marszalek;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.marszalek.service.WordOfDayService;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SchedulerHandler implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final WordOfDayService wordOfDayService;

    public SchedulerHandler() {
        // Bootstrap Micronaut context to get access to beans
        var apiFunction = new ApiGatewayProxyRequestEventFunction();
        this.wordOfDayService = apiFunction
                .getApplicationContext()
                .getBean(WordOfDayService.class);
    }

    @Override
    public Map<String, String> handleRequest(Map<String, Object> event, Context context) {
        log.info("Scheduler triggered by EventBridge");
        var word = wordOfDayService.fetchAndStoreWordOfDay();
        log.info("Word of the day saved: {}", word.getWord());
        return Map.of("message", "Word saved: " + word.getWord());
    }
}
