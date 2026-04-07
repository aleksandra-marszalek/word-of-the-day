package com.marszalek.config;

import com.marszalek.model.WordOfDay;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Factory
public class DynamoDbConfig {

    private static final TableSchema<WordOfDay> WORD_OF_DAY_SCHEMA = StaticTableSchema.builder(WordOfDay.class)
            .newItemSupplier(WordOfDay::new)
            .addAttribute(String.class, a -> a.name("PK")
                    .getter(WordOfDay::getPk)
                    .setter(WordOfDay::setPk)
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("SK")
                    .getter(WordOfDay::getDate)
                    .setter(WordOfDay::setDate)
                    .tags(StaticAttributeTags.primarySortKey()))
            .addAttribute(String.class, a -> a.name("word")
                    .getter(WordOfDay::getWord)
                    .setter(WordOfDay::setWord))
            .addAttribute(String.class, a -> a.name("definition")
                    .getter(WordOfDay::getDefinition)
                    .setter(WordOfDay::setDefinition))
            .addAttribute(String.class, a -> a.name("partOfSpeech")
                    .getter(WordOfDay::getPartOfSpeech)
                    .setter(WordOfDay::setPartOfSpeech))
            .addAttribute(String.class, a -> a.name("phonetic")
                    .getter(WordOfDay::getPhonetic)
                    .setter(WordOfDay::setPhonetic))
            .addAttribute(String.class, a -> a.name("audioUrl")
                    .getter(WordOfDay::getAudioUrl)
                    .setter(WordOfDay::setAudioUrl))
            .addAttribute(String.class, a -> a.name("fetchedAt")
                    .getter(WordOfDay::getFetchedAt)
                    .setter(WordOfDay::setFetchedAt))
            .build();

    @Singleton
    public DynamoDbClient dynamoDbClient(
            @Value("${aws.region:eu-west-1}") String region,
            @Value("${aws.dynamodb.endpoint-override:}") String endpointOverride) {

        var builder = DynamoDbClient.builder()
                .region(Region.of(region));

        if (!endpointOverride.isBlank()) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }

    @Singleton
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    }

    @Singleton
    public DynamoDbTable<WordOfDay> wordTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("word-of-the-day", WORD_OF_DAY_SCHEMA);
    }
}

