package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Service implements Parcelable, Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    private String name, specifics, description, category, reservationType, location, creator, status, workingHoursStart, workingHoursEnd;
    private String[] images, eventTypes;
    private double price, discount;
    private boolean isVisible, isAvailable, isDeleted;
    private int duration, minEngagement, maxEngagement, reservationDeadline, cancellationDeadline;

    public Service(int id, String name, String description, double price, double discount, String[] images, boolean isVisible, boolean isAvailable, String category, String[] eventTypes, String location, String creator, boolean isDeleted, String status, String reservationType, String specifics, int duration, int minEngagement, int maxEngagement, int reservationDeadline, int cancellationDeadline, String workingHoursStart, String workingHoursEnd) {
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
        id = in.readInt();
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
        dest.writeInt(id);
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

    public int getId() {
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

    public String getWorkingHoursStart() {
        return workingHoursStart;
    }

    public String getWorkingHoursEnd() {
        return workingHoursEnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setReservationType(String reservationType) {
        this.reservationType = reservationType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWorkingHoursStart(String workingHoursStart) {
        this.workingHoursStart = workingHoursStart;
    }

    public void setWorkingHoursEnd(String workingHoursEnd) {
        this.workingHoursEnd = workingHoursEnd;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public void setEventTypes(String[] eventTypes) {
        this.eventTypes = eventTypes;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMinEngagement(int minEngagement) {
        this.minEngagement = minEngagement;
    }

    public void setMaxEngagement(int maxEngagement) {
        this.maxEngagement = maxEngagement;
    }

    public void setReservationDeadline(int reservationDeadline) {
        this.reservationDeadline = reservationDeadline;
    }

    public void setCancellationDeadline(int cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }
}
