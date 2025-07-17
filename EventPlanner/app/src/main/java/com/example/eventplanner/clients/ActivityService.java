package com.example.eventplanner.clients;

import com.example.eventplanner.models.EventActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ActivityService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("agenda/{eventId}")
    Call<ArrayList<EventActivity>> getAll(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("agenda/create/{eventId}")
    Call<EventActivity> add(@Path("eventId") int eventId, @Body EventActivity activity);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("agenda/edit/{id}")
    Call<Void> edit(@Path("id") int id, @Body EventActivity activity);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("agenda/delete/{id}")
    Call<Void> deleteById(@Path("id") int id);
}
