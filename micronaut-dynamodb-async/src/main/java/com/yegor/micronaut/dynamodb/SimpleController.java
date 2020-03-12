package com.yegor.micronaut.dynamodb;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.util.concurrent.CompletableFuture;

@Controller("/event")
public class SimpleController {

    private final DynamoDBService dynamoDBService;

    public SimpleController(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    @Get("/{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletableFuture<HttpResponse<Event>> getEvent(@PathVariable String eventId) {
        CompletableFuture<Event> event = dynamoDBService.getEvent(eventId);
        return event.thenApply(HttpResponse::ok);
    }

    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletableFuture<HttpResponse<String>> saveEvent(@Body String body) {
        CompletableFuture<String> event = dynamoDBService.saveEvent(body);
        return event.thenApply(HttpResponse::created);
    }
}
