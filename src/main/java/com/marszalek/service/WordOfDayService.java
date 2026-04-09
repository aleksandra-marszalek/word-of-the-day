package com.marszalek.service;

import com.marszalek.dto.HintDTO;
import com.marszalek.dto.RevealDTO;
import com.marszalek.dto.WordOfDayDTO;
import com.marszalek.model.WordOfDay;

import java.util.List;

public interface WordOfDayService {
    WordOfDay fetchAndStoreWordOfDay();

    WordOfDayDTO getValidWordOfDay();

    boolean checkGuess(String guess);

    HintDTO getHint(List<Integer> revealedPositions);

    RevealDTO reveal();
}