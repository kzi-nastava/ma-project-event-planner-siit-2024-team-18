package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventType {
    @SerializedName("id")
    @Expose
    private int id;
    private String name, description;
    private String[] categories;

    public EventType(int id, String name, String description, String[] categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getCategories() {
        return categories;
    }
}
