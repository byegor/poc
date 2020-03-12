package com.yegor.micronaut.dynamodb;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxStreamingHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@ExtendWith(LocalDynamoDbExtension.class)
public class SimpleControllerTest {

    @Inject
    @Client("/event")
    RxStreamingHttpClient client;

    @Inject
    DynamoDBService dynamoDBService;


    @Test
    public void getEventsTest() throws ExecutionException, InterruptedException {
        //add event to database so we can query it via http
        String eventBody = "testMessage";
        String eventId = dynamoDBService.saveEvent(eventBody).get();
        HttpRequest request = HttpRequest.GET("/" + eventId);
        HttpResponse<List<Event>> rsp = client.toBlocking().exchange(request, Argument.listOf(Event.class));

        assertEquals(HttpStatus.OK, rsp.getStatus());
        List<Event> body = rsp.body();
        assertEquals(1, body.size());
        assertEquals(eventBody, body.get(0).getBody());
    }

    @Test
    public void saveEventTest() throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.POST("/", "postBody");
        HttpResponse<String> rsp = client.toBlocking().exchange(request, Argument.of(String.class));
        Optional<String> id = rsp.getBody();
        assertTrue(id.isPresent());

        Event event = dynamoDBService.getEvent(id.get()).get();
        assertEquals(id.get(), event.getId());
        assertEquals("postBody", event.getBody());
    }


}