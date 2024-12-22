package com.example.eventplanner.clients;

import com.example.eventplanner.models.Reservation;

import java.util.Collection;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReservationService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("reservations/{serviceId}")
    Call<Collection<Reservation>> getReservationsByServiceId(@Path("serviceId") int serviceId);
}
