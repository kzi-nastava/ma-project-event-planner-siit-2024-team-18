package com.example.eventplanner.clients;

import com.example.eventplanner.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @GET("/api/user-profiles")
    Call<User> getLoggedUser();

    @GET("/api/user-profiles/all")
    Call<ArrayList<User>> getAllUsers();

    @POST("/api/event-organizers")
    Call<Void> eventOrganizerRegistration(@Body User user);

    @POST("/api/service-product-providers")
    Call<Void> serviceProductProviderRegistration(@Body User user);

    @POST("/api/event-organizers/fast-registration")
    Call<Void> fastEventOrganizerRegistration(@Body User user);

    @POST("/api/service-product-providers/fast-registration")
    Call<Void> fastServiceProductProviderRegistration(@Body User user);
}
