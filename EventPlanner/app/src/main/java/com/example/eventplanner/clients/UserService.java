package com.example.eventplanner.clients;

import com.example.eventplanner.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/api/event-organizers")
    Call<Void> eventOrganizerRegistration(@Body User user);

    @POST("/api/service-product-providers")
    Call<Void> serviceProductProviderRegistration(@Body User user);
}
