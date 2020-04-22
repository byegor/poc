package com.yegor.micronaut.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class DynamoDBService {

    public static final String TABLE_NAME = "events";
    public static final String ID_COLUMN = "id";
    public static final String BODY_COLUMN = "body";

    private final AmazonDynamoDBAsync client;

    public DynamoDBService(AmazonDynamoDBAsync client) {
        this.client = client;
    }


    @PostConstruct
    public void createTableIfNotExists() {
        if (!isTableExists()) {
            createTable();
        }
    }

    public Maybe<Event> getEvent(String eventId) {
        Map<String, AttributeValue> searchCriteria = new HashMap<>();
        searchCriteria.put(ID_COLUMN, new AttributeValue().withS(eventId));

        GetItemRequest request = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(searchCriteria)
                .withAttributesToGet(BODY_COLUMN);
        return Maybe.fromFuture(client.getItemAsync(request))
                .subscribeOn(Schedulers.io())
                .filter(result -> result.getItem() != null)
                .map(result -> new Event(eventId, result.getItem().get(BODY_COLUMN).getS()));
    }

    public Single<String> saveEvent(String eventBody) {
        String id = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ID_COLUMN, new AttributeValue().withS(id));
        item.put(BODY_COLUMN, new AttributeValue().withS(eventBody));

        PutItemRequest putRequest = new PutItemRequest()
                .withTableName(TABLE_NAME)
                .withItem(item);

        return Single.fromFuture(client.putItemAsync(putRequest))
                .subscribeOn(Schedulers.io())
                .map(result -> id);
    }

    private boolean isTableExists() {
        ListTablesRequest tablesRequest = new ListTablesRequest()
                .withExclusiveStartTableName(TABLE_NAME);
        ListTablesResult result = client.listTables(tablesRequest);
        return result.getTableNames().contains(TABLE_NAME);
    }

    private CreateTableResult createTable() {
        KeySchemaElement keyDefinitions = new KeySchemaElement()
                .withAttributeName(ID_COLUMN)
                .withKeyType(KeyType.HASH);

        AttributeDefinition keyType = new AttributeDefinition()
                .withAttributeName(ID_COLUMN)
                .withAttributeType(ScalarAttributeType.S);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(TABLE_NAME)
                .withKeySchema(keyDefinitions)
                .withAttributeDefinitions(keyType)
                .withBillingMode(BillingMode.PAY_PER_REQUEST);

        return client.createTable(request);
    }
}
