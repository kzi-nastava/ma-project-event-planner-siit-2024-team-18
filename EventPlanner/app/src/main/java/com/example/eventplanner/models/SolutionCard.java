package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SolutionCard implements Parcelable, Serializable {

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

    @SerializedName("locationName")
    @Expose
    private String locationName;

    @SerializedName("isAvailable")
    @Expose
    private boolean isAvailable;

    @SerializedName("cardImage")
    @Expose
    private String cardImage;

    @SerializedName("solutionType")
    @Expose
    private String solutionType;

    public SolutionCard() {
    }

    protected SolutionCard(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        locationName = in.readString();
        isAvailable = in.readByte() != 0;
        cardImage = in.readString();
        solutionType = in.readString();
    }

    public static final Creator<SolutionCard> CREATOR = new Creator<SolutionCard>() {
        @Override
        public SolutionCard createFromParcel(Parcel in) {
            return new SolutionCard(in);
        }

        @Override
        public SolutionCard[] newArray(int size) {
            return new SolutionCard[size];
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }

    @NonNull
    @Override
    public String toString() {
        return "SolutionCard{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", locationName='" + locationName + '\'' +
                ", isAvailable=" + isAvailable +
                ", cardImage='" + cardImage + '\'' +
                ", solutionType='" + solutionType + '\'' +
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
        dest.writeString(locationName);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(cardImage);
        dest.writeString(solutionType);
    }
}