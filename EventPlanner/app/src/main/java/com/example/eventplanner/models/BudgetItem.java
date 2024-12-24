package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BudgetItem {
    @SerializedName("id")
    @Expose
    private int id;
    private String categoryName;
    private int maxAmount;

    public BudgetItem(String categoryName, int maxAmount) {
        this.categoryName = categoryName;
        this.maxAmount = maxAmount;
    }

    @Override
    public String toString() {
        return categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return categoryName;
    }

    public void setCategory(String category) {
        this.categoryName = category;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }
}
