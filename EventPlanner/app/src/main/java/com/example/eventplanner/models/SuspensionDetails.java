package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SuspensionDetails implements Serializable {

    @SerializedName("userId")
    @Expose
    private int userId;

    @SerializedName("suspensionEndDate")
    @Expose
    private String suspensionEndDate;

    @SerializedName("timeLeft")
    @Expose
    private TimeLeft timeLeft;

    public SuspensionDetails() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSuspensionEndDate() {
        return suspensionEndDate;
    }

    public void setSuspensionEndDate(String suspensionEndDate) {
        this.suspensionEndDate = suspensionEndDate;
    }

    public TimeLeft getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(TimeLeft timeLeft) {
        this.timeLeft = timeLeft;
    }

    public static class TimeLeft implements Serializable {
        @SerializedName("seconds")
        @Expose
        private int seconds;

        @SerializedName("nanos")
        @Expose
        private int nanos;

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public int getNanos() {
            return nanos;
        }

        public void setNanos(int nanos) {
            this.nanos = nanos;
        }
    }
}
