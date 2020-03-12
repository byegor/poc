package com.yegor.micronaut.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Singleton
public class DynamoDBService {

    public static final String TABLE_NAME = "events";
    public static final String ID_COLUMN = "id";
    public static final String BODY_COLUMN = "body";

    private final DynamoDbAsyncClient client;

    public DynamoDBService(DynamoDbAsyncClient client) {
        this.client = client;
    }

    @PostConstruct
    public void createTableIfNotExists() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> tableExists = isTableExists();
        CompletableFuture<CreateTableResponse> response = tableExists.thenCompose(exists -> {
            if (!exists) {
                return createTable();
            } else {
                return CompletableFuture.completedStage(null);
            }
        });
        response.get();
    }

    public CompletableFuture<Event> getEvent(String eventId) {
        Map<String, AttributeValue> searchCriteria = new HashMap<>();
        searchCriteria.put(ID_COLUMN, AttributeValue.builder().s(eventId).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(searchCriteria)
                .attributesToGet(BODY_COLUMN)
                .build();
        CompletableFuture<GetItemResponse> response = client.getItem(request);

        return response.thenApply(item -> {
            if (!item.hasItem()) {
                return null;
            } else {
                Map<String, AttributeValue> searchResult = item.item();
                String body = searchResult.get(BODY_COLUMN).s();
                return new Event(eventId, body);
            }
        });
    }

    public CompletableFuture<String> saveEvent(String eventBody) {
        String id = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ID_COLUMN, AttributeValue.builder().s(id).build());
        item.put(BODY_COLUMN, AttributeValue.builder().s(eventBody).build());

        PutItemRequest putRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        CompletableFuture<PutItemResponse> response = client.putItem(putRequest);
        return response.thenApply(r -> id);
    }

    private CompletableFuture<Boolean> isTableExists() {
        ListTablesRequest tablesRequest = ListTablesRequest.builder()
                .exclusiveStartTableName(TABLE_NAME).build();
        CompletableFuture<ListTablesResponse> response = client.listTables(tablesRequest);
        return response.thenApply(res -> res.tableNames().contains(TABLE_NAME));
    }

    private CompletableFuture<CreateTableResponse> createTable() {
        KeySchemaElement keyDefinitions = KeySchemaElement.builder()
                .attributeName(ID_COLUMN)
                .keyType(KeyType.HASH)
                .build();

        AttributeDefinition keyType = AttributeDefinition.builder()
                .attributeName(ID_COLUMN)
                .attributeType(ScalarAttributeType.S)
                .build();

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .keySchema(keyDefinitions)
                .attributeDefinitions(keyType)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        return client.createTable(request);
    }
}
