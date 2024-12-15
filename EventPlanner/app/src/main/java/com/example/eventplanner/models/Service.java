package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Service implements Parcelable, Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    private String name, specifics, description, category, reservationType, location, creator, status;
    private String[] images, eventTypes;
    private double price, discount;
    private boolean isVisible, isAvailable, isDeleted;
    private int duration, minEngagement, maxEngagement, reservationDeadline, cancellationDeadline;
    private LocalTime workingHoursStart, workingHoursEnd;

    public Service(String id, String name, String description, double price, double discount, String[] images, boolean isVisible, boolean isAvailable, String category, String[] eventTypes, String location, String creator, boolean isDeleted, String status, String reservationType, String specifics, int duration, int minEngagement, int maxEngagement, int reservationDeadline, int cancellationDeadline, LocalTime workingHoursStart, LocalTime workingHoursEnd) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.images = images;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
        this.category = category;
        this.eventTypes = eventTypes;
        this.location = location;
        this.creator = creator;
        this.isDeleted = isDeleted;
        this.status = status;
        this.reservationType = reservationType;
        this.specifics = specifics;
        this.duration = duration;
        this.minEngagement = minEngagement;
        this.maxEngagement = maxEngagement;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.workingHoursStart = workingHoursStart;
        this.workingHoursEnd = workingHoursEnd;
    }

    protected Service(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        images = in.createStringArray();
        isVisible = in.readByte() != 0;
        isAvailable = in.readByte() != 0;
        category = in.readString();
        eventTypes = in.createStringArray();
        location = in.readString();
        creator = in.readString();
        isDeleted = in.readByte() != 0;
        status = in.readString();
        reservationType = in.readString();
        specifics = in.readString();
        duration = in.readInt();
        minEngagement = in.readInt();
        maxEngagement = in.readInt();
        reservationDeadline = in.readInt();
        cancellationDeadline = in.readInt();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeStringArray(images);
        dest.writeByte((byte) (isVisible ? 1 : 0));
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(category);
        dest.writeStringArray(eventTypes);
        dest.writeString(location);
        dest.writeString(creator);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(status);
        dest.writeString(reservationType);
        dest.writeString(specifics);
        dest.writeInt(duration);
        dest.writeInt(minEngagement);
        dest.writeInt(maxEngagement);
        dest.writeInt(reservationDeadline);
        dest.writeInt(cancellationDeadline);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecifics() {
        return specifics;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getReservationType() {
        return reservationType;
    }

    public String getLocation() {
        return location;
    }

    public String getCreator() {
        return creator;
    }

    public String getStatus() {
        return status;
    }

    public String[] getImages() {
        return images;
    }

    public List<String> getEventTypes() {
        return Arrays.asList(eventTypes);
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public int getDuration() {
        return duration;
    }

    public int getMinEngagement() {
        return minEngagement;
    }

    public int getMaxEngagement() {
        return maxEngagement;
    }

    public int getReservationDeadline() {
        return reservationDeadline;
    }

    public int getCancellationDeadline() {
        return cancellationDeadline;
    }

    public LocalTime getWorkingHoursStart() {
        return workingHoursStart;
    }

    public LocalTime getWorkingHoursEnd() {
        return workingHoursEnd;
    }

}
