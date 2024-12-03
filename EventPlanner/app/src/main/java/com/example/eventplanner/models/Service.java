package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Service implements Parcelable {
    private String id, name, specifics, description, category, eventType, reservationType;
    private double price, discount;
    private boolean available;
    private LocalDateTime reservationDate, cancellationDate;
    private int duration;

    public Service(String id, String name, String specifics, String category, String eventType, double price, double discount, boolean available, LocalDateTime reservationDate, LocalDateTime cancellationDate, int duration, String reservationType) {
        this.id = id;
        this.name = name;
        this.specifics = specifics;
        this.category = category;
        this.eventType = eventType;
        this.price = price;
        this.discount = discount;
        this.available = available;
        this.reservationDate = reservationDate;
        this.cancellationDate = cancellationDate;
        this.duration = duration;
        this.reservationType = reservationType;
    }

    protected Service(Parcel in) {
        id = in.readString();
        name = in.readString();
        specifics = in.readString();
        description = in.readString();
        category = in.readString();
        eventType = in.readString();
        reservationType = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        available = in.readByte() != 0;
        duration = in.readInt();
        reservationDate = (LocalDateTime) in.readSerializable();
        cancellationDate = (LocalDateTime) in.readSerializable();
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
        dest.writeString(specifics);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(eventType);
        dest.writeString(reservationType);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeInt(duration);
        dest.writeSerializable(reservationDate);
        dest.writeSerializable(cancellationDate);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecifics() { return specifics; }
    public String getCategory() { return category; }
    public String getEventType() { return eventType; }
    public double getPrice() { return price; }
    public double getDiscount() { return discount; }
    public boolean isAvailable() { return available; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public LocalDateTime getCancellationDate() { return cancellationDate; }
    public int getDuration() { return duration; }
    public String getReservationType() { return reservationType; }
}
