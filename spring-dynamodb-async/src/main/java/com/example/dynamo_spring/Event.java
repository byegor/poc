package com.example.dynamo_spring;


import java.util.Objects;

public class Event {
    String uuid;
    String body;

    public Event() {
    }

    public Event(String uuid, String body) {
        this.uuid = uuid;
        this.body = body;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return uuid.equals(event.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
