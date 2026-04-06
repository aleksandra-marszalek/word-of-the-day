package com.marszalek.error;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ValidationException;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class ValidationExceptionHandler
        implements ExceptionHandler<ValidationException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ValidationException exception) {
        return HttpResponse.badRequest(new ErrorResponse(exception.getMessage()));
    }
}
