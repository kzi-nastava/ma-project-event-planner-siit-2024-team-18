package com.example.eventplanner.fragments;

public interface FilterApplyListener {
    void onFilterApplied(String category, String eventType, int minPrice, int maxPrice, String isAvailable);
}
