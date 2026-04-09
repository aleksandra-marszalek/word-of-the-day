package com.marszalek.controller;

import com.marszalek.dto.*;
import com.marszalek.service.WordOfDayService;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

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

    @Get("/hint")
    public HintDTO getHint(@QueryValue List<Integer> revealed) {
        return wordOfDayService.getHint(revealed);
    }

    @Get("/reveal")
    public RevealDTO reveal() {
        return wordOfDayService.reveal();
    }
}
