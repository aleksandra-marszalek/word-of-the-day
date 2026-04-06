package com.marszalek.error;

import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Singleton;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class WordNotFoundExceptionHandler
        implements ExceptionHandler<WordNotFoundException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, WordNotFoundException exception) {
        return HttpResponse.notFound(new ErrorResponse(exception.getMessage()));
    }
}
