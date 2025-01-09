package com.example.eventplanner.clients;

import com.example.eventplanner.models.EventType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventTypeService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("event-types")
    Call<ArrayList<EventType>> getAll();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("event-types/create")
    Call<Void> create(@Body EventType eventType);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("event-types/edit/{id}")
    Call<Void> update(@Path("id") int id, @Body EventType eventType);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("event-types/delete/{id}")
    Call<Void> delete(@Path("id") int id);
}
