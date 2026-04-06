package com.marszalek.controller;

import com.marszalek.dto.GuessRequestDTO;
import com.marszalek.dto.GuessResponseDTO;
import com.marszalek.dto.WordOfDayDTO;
import com.marszalek.service.WordOfDayService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;

@Controller("/api/v1/word")
public class WordController {

    private final WordOfDayService wordOfDayService;

    public WordController(WordOfDayService wordOfDayService) {
        this.wordOfDayService = wordOfDayService;
    }

    @Get("/today")
    public WordOfDayDTO getWord() {
        return wordOfDayService.getValidWordOfDay();
    }

    @Post("/guess")
    public GuessResponseDTO guessWord(@Valid @Body GuessRequestDTO dto) {
        return new GuessResponseDTO(wordOfDayService.checkGuess(dto.guess()));
    }
}
