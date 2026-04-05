package com.marszalek.repository;

import com.marszalek.model.WordOfDay;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;

@Singleton
public class DynamoDbWordRepository implements WordRepository {

    public DynamoDbWordRepository(DynamoDbTable<WordOfDay> wordTable) {
        this.wordTable = wordTable;
    }

    private final DynamoDbTable<WordOfDay> wordTable;
    private final static String PARTITION_KEY = "WORD";

    @Override
    public void save(WordOfDay wordOfDay) {
        wordOfDay.setPk(PARTITION_KEY);
        wordTable.putItem(wordOfDay);
    }

    @Override
    public Optional<WordOfDay> findByDate(String date) {
        var result = wordTable.getItem(Key.builder()
                .partitionValue(PARTITION_KEY)
                .sortValue(date)
                .build());

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<WordOfDay> findMostRecent() {
        var query = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(PARTITION_KEY).build());

        var request = QueryEnhancedRequest.builder()
                .queryConditional(query)
                .scanIndexForward(false)
                .limit(1)
                .build();

        return wordTable.query(request).stream()
                .findFirst()
                .flatMap(wordOfDayPage -> wordOfDayPage.items().stream().findFirst());
    }

    @Override
    public List<WordOfDay> findByDates(String start, String end) {
        var query = QueryConditional.sortBetween(
                Key.builder().partitionValue(PARTITION_KEY).sortValue(start).build(),
                Key.builder().partitionValue(PARTITION_KEY).sortValue(end).build()
        );

        var request = QueryEnhancedRequest.builder().queryConditional(query).scanIndexForward(false).build();

        return wordTable.query(request).stream()
                .flatMap(wordOfDayPage -> wordOfDayPage.items().stream()).toList();
    }
}
