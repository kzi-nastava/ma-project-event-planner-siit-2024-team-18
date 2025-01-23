package com.example.eventplanner.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReportRequest implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("reporterFirstName")
    @Expose
    private String reporterFirstName;

    @SerializedName("reportedFirstName")
    @Expose
    private String reportedFirstName;

    @SerializedName("reporterLastName")
    @Expose
    private String reporterLastName;

    @SerializedName("reportedLastName")
    @Expose
    private String reportedLastName;

    @SerializedName("reportedDate")
    @Expose
    private LocalDateTime reportedDate;

    public ReportRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterFirstName() {
        return reporterFirstName;
    }

    public void setReporterFirstName(String reporterFirstName) {
        this.reporterFirstName = reporterFirstName;
    }

    public String getReportedFirstName() {
        return reportedFirstName;
    }

    public void setReportedFirstName(String reportedFirstName) {
        this.reportedFirstName = reportedFirstName;
    }

    public String getReporterLastName() {
        return reporterLastName;
    }

    public void setReporterLastName(String reporterLastName) {
        this.reporterLastName = reporterLastName;
    }

    public String getReportedLastName() {
        return reportedLastName;
    }

    public void setReportedLastName(String reportedLastName) {
        this.reportedLastName = reportedLastName;
    }

    public LocalDateTime getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDateTime reportedDate) {
        this.reportedDate = reportedDate;
    }
}
