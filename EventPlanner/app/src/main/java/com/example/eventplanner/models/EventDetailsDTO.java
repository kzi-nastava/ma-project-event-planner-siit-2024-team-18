package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class EventDetailsDTO implements Parcelable, Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("maxParticipants")
    @Expose
    private int maxParticipants;

    @SerializedName("privacyType")
    @Expose
    private String privacyType;

    @SerializedName("startDate")
    @Expose
    private LocalDateTime startDate;

    @SerializedName("endDate")
    @Expose
    private LocalDateTime endDate;

    @SerializedName("budget")
    @Expose
    private double budget;

    @SerializedName("images")
    @Expose
    private List<String> images;

    @SerializedName("creatorId")
    @Expose
    private int creatorId;

    @SerializedName("locationId")
    @Expose
    private int locationId;

    @SerializedName("locationName")
    @Expose
    private String locationName;

    @SerializedName("eventTypeId")
    @Expose
    private int eventTypeId;

    @SerializedName("eventTypeName")
    @Expose
    private String eventTypeName;

    public EventDetailsDTO() {
    }

    protected EventDetailsDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        maxParticipants = in.readInt();
        privacyType = in.readString();
        startDate = (LocalDateTime) in.readSerializable();
        endDate = (LocalDateTime) in.readSerializable();
        budget = in.readDouble();
        images = in.createStringArrayList();
        creatorId = in.readInt();
        locationId = in.readInt();
        locationName = in.readString();
        eventTypeId = in.readInt();
        eventTypeName = in.readString();
    }

    public static final Creator<EventDetailsDTO> CREATOR = new Creator<EventDetailsDTO>() {
        @Override
        public EventDetailsDTO createFromParcel(Parcel in) {
            return new EventDetailsDTO(in);
        }

        @Override
        public EventDetailsDTO[] newArray(int size) {
            return new EventDetailsDTO[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", privacyType='" + privacyType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", budget=" + budget +
                ", images=" + images +
                ", creatorId=" + creatorId +
                ", locationId=" + locationId +
                ", locationName='" + locationName + '\'' +
                ", eventTypeId=" + eventTypeId +
                ", eventTypeName='" + eventTypeName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(maxParticipants);
        dest.writeString(privacyType);
        dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);
        dest.writeDouble(budget);
        dest.writeStringList(images);
        dest.writeInt(creatorId);
        dest.writeInt(locationId);
        dest.writeString(locationName);
        dest.writeInt(eventTypeId);
        dest.writeString(eventTypeName);
    }
}
