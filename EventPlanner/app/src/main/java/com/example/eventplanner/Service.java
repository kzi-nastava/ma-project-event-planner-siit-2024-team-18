package com.example.eventplanner;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Service implements Serializable {
    private String id, name, specifics, description, category, eventType, reservationType;
    private double price, discount;
    private boolean available;
    private LocalDateTime reservationDate, cancellationDate;
    private int duration;

    public Service(String id, String name, String specifics, String category, String eventType, double price, double discount, boolean available, LocalDateTime reservationDate, LocalDateTime cancellationDate, int duration, String reservationType)  {
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
