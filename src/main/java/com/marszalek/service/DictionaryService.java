package com.marszalek.service;

import com.marszalek.model.DictionaryData;

public interface DictionaryService {
    DictionaryData getWordDefinitionData(String word);
}
