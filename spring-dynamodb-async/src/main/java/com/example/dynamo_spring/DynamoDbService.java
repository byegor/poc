package com.example.dynamo_spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DynamoDbService {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbService.class);

    public static final String TABLE_NAME = "events";
    public static final String ID_COLUMN = "id";
    public static final String BODY_COLUMN = "body";

    final DynamoDbAsyncClient client;


    public DynamoDbService(DynamoDbAsyncClient client) {
        this.client = client;
    }

    @PostConstruct
    public void createTableIfNeeded() throws ExecutionException, InterruptedException {
        ListTablesRequest request = ListTablesRequest.builder().exclusiveStartTableName(TABLE_NAME).build();
        CompletableFuture<ListTablesResponse> listTableResponse = client.listTables(request);

        CompletableFuture<CreateTableResponse> createTableRequest = listTableResponse
                .thenCompose(response -> {
                    boolean tableExist = response.tableNames().contains(TABLE_NAME);
                    if (!tableExist) {
                        return createTable();
                    } else {
                        return CompletableFuture.completedFuture(null);
                    }
                });

        CreateTableResponse createTableResponse = createTableRequest.get();
        System.out.println(createTableRequest);
    }

    public CompletableFuture<PutItemResponse> saveEvent(Event event) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ID_COLUMN, AttributeValue.builder().s(event.getUuid()).build());
        item.put(BODY_COLUMN, AttributeValue.builder().s(event.getBody()).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        return client.putItem(putItemRequest);
    }

    public CompletableFuture<Optional<Event>> getEvent(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(ID_COLUMN, AttributeValue.builder().s(id).build());

        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .attributesToGet(BODY_COLUMN)
                .build();

        return client.getItem(getRequest).thenApply(item -> {
            if (!item.hasItem()) {
                return Optional.empty();
            } else {
                Map<String, AttributeValue> itemAttr = item.item();
                String body = itemAttr.get(BODY_COLUMN).s();
                return Optional.of(new Event(id, body));
            }
        });
    }

    private CompletableFuture<CreateTableResponse> createTable() {

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(TABLE_NAME)

                .keySchema(KeySchemaElement.builder().attributeName(ID_COLUMN).keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName(ID_COLUMN).attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        return client.createTable(request);
    }
}

