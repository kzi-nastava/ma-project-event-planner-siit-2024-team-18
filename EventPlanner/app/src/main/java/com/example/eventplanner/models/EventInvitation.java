package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventInvitation implements Parcelable, Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("date")
    @Expose
    private LocalDateTime date;

    @SerializedName("locationName")
    @Expose
    private String locationName;

    @SerializedName("eventTypeName")
    @Expose
    private String eventTypeName;

    @SerializedName("creatorFirstName")
    @Expose
    private String creatorFirstName;

    @SerializedName("creatorLastName")
    @Expose
    private String creatorLastName;

    @SerializedName("images")
    @Expose
    private ArrayList<String> images;

    public EventInvitation() {
    }

    protected EventInvitation(Parcel in) {
        name = in.readString();
        description = in.readString();
        date = (LocalDateTime) in.readSerializable();
        locationName = in.readString();
        eventTypeName = in.readString();
        creatorFirstName = in.readString();
        creatorLastName = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<EventInvitation> CREATOR = new Creator<EventInvitation>() {
        @Override
        public EventInvitation createFromParcel(Parcel in) {
            return new EventInvitation(in);
        }

        @Override
        public EventInvitation[] newArray(int size) {
            return new EventInvitation[size];
        }
    };

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public void setCreatorFirstName(String creatorFirstName) {
        this.creatorFirstName = creatorFirstName;
    }

    public String getCreatorLastName() {
        return creatorLastName;
    }

    public void setCreatorLastName(String creatorLastName) {
        this.creatorLastName = creatorLastName;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventInvitation{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + date +
                ", locationName='" + locationName + '\'' +
                ", eventTypeName='" + eventTypeName + '\'' +
                ", creatorFirstName='" + creatorFirstName + '\'' +
                ", creatorLastName='" + creatorLastName + '\'' +
                ", images=" + images +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeSerializable(date);
        dest.writeString(locationName);
        dest.writeString(eventTypeName);
        dest.writeString(creatorFirstName);
        dest.writeString(creatorLastName);
        dest.writeStringList(images);
    }
}
