package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventType {
    @SerializedName("id")
    @Expose
    private int id;
    private String name, description;
    private List<String> categories;

    public EventType(String name) {
        this.name = name;
    }

    public EventType(String name, String description, List<String> categories) {
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

    public String getDescription() { return description; }

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
