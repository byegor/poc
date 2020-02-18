package com.example.dynamo_spring;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/event")
public class SimpleController {

    final DynamoDbService dynamoDbService;

    public SimpleController(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    @GetMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Optional<Event>> getEvent(@PathVariable String eventId) {
        CompletableFuture<Optional<Event>> eventFuture = dynamoDbService.getEvent(eventId);
        return Mono.fromCompletionStage(eventFuture);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveEvent(@RequestBody Event event) {
        dynamoDbService.saveEvent(event);
    }
}
