package com.example.eventplanner.models;

import java.time.LocalDateTime;

public class CalendarEvent {
    private int id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;

    public CalendarEvent(int id, String title, LocalDateTime start, LocalDateTime end, String location) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
