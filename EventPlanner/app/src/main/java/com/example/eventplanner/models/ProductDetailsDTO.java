package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductDetailsDTO implements Parcelable, Serializable {

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

    @SerializedName("isBought")
    @Expose
    private boolean isBought;

    public ProductDetailsDTO() {
    }

    protected ProductDetailsDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        images = in.createStringArrayList();
        isVisible = in.readByte() != 0;
        isAvailable = in.readByte() != 0;
        status = in.readString();
        isBought = in.readByte() != 0;
    }

    public static final Creator<ProductDetailsDTO> CREATOR = new Creator<ProductDetailsDTO>() {
        @Override
        public ProductDetailsDTO createFromParcel(Parcel in) {
            return new ProductDetailsDTO(in);
        }

        @Override
        public ProductDetailsDTO[] newArray(int size) {
            return new ProductDetailsDTO[size];
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

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductDetailsDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", images=" + images +
                ", isVisible=" + isVisible +
                ", isAvailable=" + isAvailable +
                ", status='" + status + '\'' +
                ", isBought=" + isBought +
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
        dest.writeByte((byte) (isBought ? 1 : 0));
    }
}
