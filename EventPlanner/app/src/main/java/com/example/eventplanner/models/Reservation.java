package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation implements Parcelable, Serializable {

    @SerializedName("reservationId")
    @Expose
    private int reservationId;

    @SerializedName("serviceId")
    @Expose
    private int serviceId;

    @SerializedName("eventId")
    @Expose
    private int eventId;

    @SerializedName("date")
    @Expose
    private LocalDate date;

    @SerializedName("timeFrom")
    @Expose
    private LocalTime timeFrom;

    @SerializedName("timeTo")
    @Expose
    private LocalTime timeTo;

    @SerializedName("status")
    @Expose
    private String status;

    public Reservation() {
    }

    public Reservation(int reservationId, int serviceId, int eventId, LocalDate date, LocalTime timeFrom, LocalTime timeTo, String status) {
        this.reservationId = reservationId;
        this.serviceId = serviceId;
        this.eventId = eventId;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.status = status;
    }

    protected Reservation(Parcel in) {
        reservationId = in.readInt();
        serviceId = in.readInt();
        eventId = in.readInt();
        date = (LocalDate) in.readSerializable();
        timeFrom = (LocalTime) in.readSerializable();
        timeTo = (LocalTime) in.readSerializable();
        status = in.readString();
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", serviceId=" + serviceId +
                ", eventId=" + eventId +
                ", date=" + date +
                ", timeFrom=" + timeFrom +
                ", timeTo=" + timeTo +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(reservationId);
        dest.writeInt(serviceId);
        dest.writeInt(eventId);
        dest.writeSerializable(date);
        dest.writeSerializable(timeFrom);
        dest.writeSerializable(timeTo);
        dest.writeString(status);
    }
}
