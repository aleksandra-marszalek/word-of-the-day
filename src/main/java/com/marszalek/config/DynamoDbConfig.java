package com.marszalek.config;

import com.marszalek.model.WordOfDay;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Factory
public class DynamoDbConfig {

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
        return enhancedClient.table("word-of-the-day", TableSchema.fromBean(WordOfDay.class));
    }
}

