package com.example.eventplanner.models;

public class FilterOption {
    private final String value;
    private final String label;

    public FilterOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
