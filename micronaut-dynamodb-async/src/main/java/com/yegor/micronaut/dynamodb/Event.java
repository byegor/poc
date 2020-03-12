package com.yegor.micronaut.dynamodb;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Event {
    private final String id;
    private final String body;

    public Event(String id, String body) {
        this.id = id;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }
}
