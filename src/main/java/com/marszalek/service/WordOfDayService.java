package com.marszalek.service;

import com.marszalek.model.WordOfDay;

public interface WordOfDayService {

    WordOfDay fetchAndStoreWordOfDay();
}