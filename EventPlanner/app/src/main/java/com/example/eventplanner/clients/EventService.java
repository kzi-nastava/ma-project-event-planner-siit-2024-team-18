package com.example.eventplanner.clients;

import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.PagedResponse;

import java.time.LocalDate;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/top-events")
    Call<Collection<EventCard>> getTop5Events();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/{id}")
    Call<Event> getEventDetailsById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/events-all")
    Call<Collection<EventCard>> getAllByCreator();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events")
    Call<PagedResponse<EventCard>> searchAndFilterEvents(
            @Query("keyword") String keyword,
            @Query("city") String city,
            @Query("startDate") LocalDate startDate,
            @Query("endDate") LocalDate endDate,
            @Query("country") String country,
            @Query("maxParticipants") Integer maxParticipants,
            @Query("budget") Double budget,
            @Query("eventType") String eventType,
            @Query("organizerFirstName") String organizerFirstName,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") String sortDirection
    );
}
