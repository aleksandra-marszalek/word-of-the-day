package com.marszalek.repository;

import com.marszalek.model.WordOfDay;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbWordRepositoryTest implements TestPropertyProvider {

    @Container
    static final GenericContainer<?> DYNAMO =
            new GenericContainer<>("amazon/dynamodb-local:latest")
                    .withExposedPorts(8000);
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DynamoDbWordRepositoryTest.class);

    @Override
    public Map<String, String> getProperties() {
        DYNAMO.start();
        return Map.of(
                "aws.dynamodb.endpoint-override",
                "http://localhost:" + DYNAMO.getMappedPort(8000),
                "aws.region", "eu-west-1"
        );
    }

    @Inject
    DynamoDbTable<WordOfDay> table;

    @Inject
    WordRepository repository;

    @BeforeEach
    void createTable() {
        try {
            table.deleteTable();
        } catch (Exception e) {
            log.info("No table to delete yet, will proceed to create a new table.");
        }
        table.createTable();
    }

    @Test
    void savesAndRetrievesWordByDate() {
        String word = "hallucination";

        var wordOfDay = new WordOfDay();
        wordOfDay.setWord(word);
        wordOfDay.setDate("2026-04-05");

        repository.save(wordOfDay);

        var result = repository.findByDate("2026-04-05");

        assertEquals(word, result.get().getWord());
        assertEquals("WORD", result.get().getPk());
    }

    @Test
    void findsMostRecentWord() {
        String word = "hallucination";

        var wordOfDay = new WordOfDay();
        wordOfDay.setWord(word);
        wordOfDay.setDate("2026-04-04");

        repository.save(wordOfDay);

        String wordNew = "extrovert";

        var wordOfDayNew = new WordOfDay();
        wordOfDayNew.setWord(wordNew);
        wordOfDayNew.setDate("2026-04-05");

        repository.save(wordOfDayNew);

        var result = repository.findMostRecent();

        assertEquals(wordNew, result.get().getWord());
    }

    @Test
    void returnsEmptyWhenDateNotFound() {
        String date = "2020-04-20";
        var response = repository.findByDate(date);

        assertTrue(response.isEmpty());
    }
}