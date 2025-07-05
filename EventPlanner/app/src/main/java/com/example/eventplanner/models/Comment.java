package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("date")
    @Expose
    private LocalDateTime date;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("comentatorId")
    @Expose
    private int comentatorId;

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getComentatorId() {
        return comentatorId;
    }

    public void setComentatorId(int comentatorId) {
        this.comentatorId = comentatorId;
    }
}
