package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Grade {
    @SerializedName("id")
    @Expose
    private int id;
    private int value;
    private String comment;

    public Grade(int value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
