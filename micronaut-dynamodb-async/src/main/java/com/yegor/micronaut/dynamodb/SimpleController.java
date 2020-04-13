package com.yegor.micronaut.dynamodb;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Controller("/event")
public class SimpleController {

    private final DynamoDBService dynamoDBService;

    public SimpleController(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    @Get("/{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Maybe<Event> getEvent(@PathVariable String eventId) {
        Maybe<Event> event = dynamoDBService.getEvent(eventId);
        return event;
    }

    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Single<String> saveEvent(@Body String body) {
        Single<String> event = dynamoDBService.saveEvent(body);
        return event;
    }
}
