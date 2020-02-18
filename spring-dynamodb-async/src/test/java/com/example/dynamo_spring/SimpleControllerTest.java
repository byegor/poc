package com.example.dynamo_spring;


import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class SimpleControllerTest {

    @ClassRule
    public static LocalDynamoDbRule dynamoDbRule = new LocalDynamoDbRule();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getEvent() {
        // Create a GET request to test an endpoint
        webTestClient
                .get().uri("/event/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // and use the dedicated DSL to test assertions against the response
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("null");
    }

    @Test
    public void saveEvent() throws InterruptedException {
        Event event = new Event("10", "event");
        webTestClient
                .post().uri("/event/")
                .body(BodyInserters.fromValue(event))
                .exchange()
                .expectStatus().isOk();
        Thread.sleep(1500);
        webTestClient
                .get().uri("/event/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // and use the dedicated DSL to test assertions against the response
                .expectStatus().isOk()
                .expectBody(Event.class).isEqualTo(event);
    }
}