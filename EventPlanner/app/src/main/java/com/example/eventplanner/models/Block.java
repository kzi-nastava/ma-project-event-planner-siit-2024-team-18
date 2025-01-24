package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Block implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("blockedDate")
    @Expose
    private LocalDateTime blockedDate;

    @SerializedName("blockerId")
    @Expose
    private int blockerId;

    @SerializedName("blockedId")
    @Expose
    private int blockedId;

    public Block() {
    }

    public Block(int blockerId, int blockedId, LocalDateTime blockedDate) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.blockedDate = blockedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getBlockedDate() {
        return blockedDate;
    }

    public void setBlockedDate(LocalDateTime blockedDate) {
        this.blockedDate = blockedDate;
    }

    public int getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(int blockerId) {
        this.blockerId = blockerId;
    }

    public int getBlockedId() {
        return blockedId;
    }

    public void setBlockedId(int blockedId) {
        this.blockedId = blockedId;
    }
}
