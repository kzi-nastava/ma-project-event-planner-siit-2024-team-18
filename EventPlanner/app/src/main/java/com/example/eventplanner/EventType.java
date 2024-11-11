package com.example.eventplanner;

public class EventType {
    private String type;

    public EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
