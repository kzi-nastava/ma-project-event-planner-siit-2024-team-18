package com.example.eventplanner.models;

public class ServiceCategory {
    private String name;

    public ServiceCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
