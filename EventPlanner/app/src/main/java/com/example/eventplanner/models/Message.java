package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Message {
    @SerializedName("id")
    @Expose
    private int id;
    private String content;
    private int chatId, senderId;
    private LocalDateTime date;
    private boolean isDeleted;

    public Message(String content, int chatId, int senderId, LocalDateTime date, boolean isDeleted) {
        this.content = content;
        this.chatId = chatId;
        this.senderId = senderId;
        this.date = date;
        this.isDeleted = isDeleted;
    }

    public Message() {}

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

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
