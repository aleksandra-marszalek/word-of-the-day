package com.marszalek.repository;

import com.marszalek.model.WordOfDay;

import java.util.List;
import java.util.Optional;

public interface WordRepository {
    void save(WordOfDay wordOfDay);

    Optional<WordOfDay> findByDate(String date);

    Optional<WordOfDay> findMostRecent();

    List<WordOfDay> findByDates(String start, String end);
}
