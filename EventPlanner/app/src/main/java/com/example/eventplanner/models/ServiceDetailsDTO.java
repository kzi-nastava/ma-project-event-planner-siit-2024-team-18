package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceDetailsDTO implements Parcelable, Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("discount")
    @Expose
    private double discount;

    @SerializedName("images")
    @Expose
    private ArrayList<String> images;

    @SerializedName("isVisible")
    @Expose
    private boolean isVisible;

    @SerializedName("isAvailable")
    @Expose
    private boolean isAvailable;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;

    @SerializedName("specifics")
    @Expose
    private String specifics;

    @SerializedName("duration")
    @Expose
    private int duration;

    @SerializedName("minEngagement")
    @Expose
    private int minEngagement;

    @SerializedName("maxEngagement")
    @Expose
    private int maxEngagement;

    @SerializedName("reservationDeadline")
    @Expose
    private int reservationDeadline;

    @SerializedName("cancellationDeadline")
    @Expose
    private int cancellationDeadline;

    @SerializedName("reservationType")
    @Expose
    private String reservationType;

    public ServiceDetailsDTO() {
    }

    protected ServiceDetailsDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        images = in.createStringArrayList();
        isVisible = in.readByte() != 0;
        isAvailable = in.readByte() != 0;
        status = in.readString();
        isDeleted = in.readByte() != 0;
        specifics = in.readString();
        duration = in.readInt();
        minEngagement = in.readInt();
        maxEngagement = in.readInt();
        reservationDeadline = in.readInt();
        cancellationDeadline = in.readInt();
        reservationType = in.readString();
    }

    public static final Creator<ServiceDetailsDTO> CREATOR = new Creator<ServiceDetailsDTO>() {
        @Override
        public ServiceDetailsDTO createFromParcel(Parcel in) {
            return new ServiceDetailsDTO(in);
        }

        @Override
        public ServiceDetailsDTO[] newArray(int size) {
            return new ServiceDetailsDTO[size];
        }
    };

    // Getters and Setters
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMinEngagement() {
        return minEngagement;
    }

    public void setMinEngagement(int minEngagement) {
        this.minEngagement = minEngagement;
    }

    public int getMaxEngagement() {
        return maxEngagement;
    }

    public void setMaxEngagement(int maxEngagement) {
        this.maxEngagement = maxEngagement;
    }

    public int getReservationDeadline() {
        return reservationDeadline;
    }

    public void setReservationDeadline(int reservationDeadline) {
        this.reservationDeadline = reservationDeadline;
    }

    public int getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(int cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public String getReservationType() {
        return reservationType;
    }

    public void setReservationType(String reservationType) {
        this.reservationType = reservationType;
    }

    @NonNull
    @Override
    public String toString() {
        return "ServiceDetailsDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", images=" + images +
                ", isVisible=" + isVisible +
                ", isAvailable=" + isAvailable +
                ", status='" + status + '\'' +
                ", isDeleted=" + isDeleted +
                ", specifics='" + specifics + '\'' +
                ", duration=" + duration +
                ", minEngagement=" + minEngagement +
                ", maxEngagement=" + maxEngagement +
                ", reservationDeadline=" + reservationDeadline +
                ", cancellationDeadline=" + cancellationDeadline +
                ", reservationType='" + reservationType + '\'' +
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
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeStringList(images);
        dest.writeByte((byte) (isVisible ? 1 : 0));
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(status);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(specifics);
        dest.writeInt(duration);
        dest.writeInt(minEngagement);
        dest.writeInt(maxEngagement);
        dest.writeInt(reservationDeadline);
        dest.writeInt(cancellationDeadline);
        dest.writeString(reservationType);
    }
}
