package com.example.eventplanner.models;

public class Event {
    private final String title;
    private final String description;
    private final int imageResourceId;

    public Event(String title, String description, int imageResourceId) {
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResourceId() { return imageResourceId; }
}
