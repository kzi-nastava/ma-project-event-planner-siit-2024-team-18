package com.example.eventplanner.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationDetails {
    private int reservationId;
    private int serviceId;
    private int eventId;
    private LocalDate date;
    private LocalTime timeFrom;
    private LocalTime timeTo;
    private String status;

    public ReservationDetails() {}

    public ReservationDetails(int reservationId, int serviceId, int eventId, LocalDate date, LocalTime timeFrom, LocalTime timeTo, String status) {
        this.reservationId = reservationId;
        this.serviceId = serviceId;
        this.eventId = eventId;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.status = status;
    }

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
}
