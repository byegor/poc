package com.example.dynamo_spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class HttpRouter {

    @Bean
    public RouterFunction<ServerResponse> eventRouter(DynamoDbService dynamoDbService) {
        EventHandler eventHandler = new EventHandler(dynamoDbService);
        return RouterFunctions
                .route(GET("/eventfn/{id}")
                        .and(accept(APPLICATION_JSON)), eventHandler::getEvent)
                .andRoute(POST("/eventfn")
                        .and(accept(APPLICATION_JSON))
                        .and(contentType(APPLICATION_JSON)), eventHandler::saveEvent);
    }

    static class EventHandler {
        private final DynamoDbService dynamoDbService;

        public EventHandler(DynamoDbService dynamoDbService) {
            this.dynamoDbService = dynamoDbService;
        }

        Mono<ServerResponse> getEvent(ServerRequest request) {
            String eventId = request.pathVariable("id");
            CompletableFuture<Event> eventGetFuture = dynamoDbService.getEvent(eventId);
            Mono<Event> eventMono = Mono.fromFuture(eventGetFuture);
            return ServerResponse.ok().body(eventMono, Event.class);
        }

        Mono<ServerResponse> saveEvent(ServerRequest request) {
            Mono<Event> eventMono = request.bodyToMono(Event.class);
            eventMono.map(dynamoDbService::saveEvent);
            return ServerResponse.ok().build();
        }
    }
}
