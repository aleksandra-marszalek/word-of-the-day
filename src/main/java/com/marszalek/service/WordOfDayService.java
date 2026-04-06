package com.marszalek.service;

import com.marszalek.dto.WordOfDayDTO;
import com.marszalek.model.WordOfDay;

public interface WordOfDayService {
    WordOfDay fetchAndStoreWordOfDay();

    WordOfDayDTO getWordOfDay();
}