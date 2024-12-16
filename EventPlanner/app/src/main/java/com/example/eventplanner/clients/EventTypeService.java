package com.example.eventplanner.clients;

import com.example.eventplanner.models.EventType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface EventTypeService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("event-types")
    Call<ArrayList<EventType>> getAll();
}
