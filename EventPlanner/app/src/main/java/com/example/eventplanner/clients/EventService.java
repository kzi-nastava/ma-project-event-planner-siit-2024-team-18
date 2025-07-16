package com.example.eventplanner.clients;

import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.PagedResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/all")
    Call<ArrayList<Event>> getAll();

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
    Call<ArrayList<EventCard>> getAllByCreator();

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

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/event-organizer")
    Call<List<EventCard>> getByEventOrganizer();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("events/delete/{id}")
    Call<Void> deleteById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android"
    })
    @Multipart
    @POST("events/create")
    Call<Void> add(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("maxParticipants") RequestBody maxParticipants,
            @Part("privacyType") RequestBody privacyType,
            @Part("startDate") RequestBody startDate,
            @Part List<MultipartBody.Part> images,
            @Part("eventType") RequestBody eventType,
            @Part("locationName") RequestBody locationName,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude
    );

    @Headers({
            "User-Agent: Mobile-Android"
    })
    @Multipart
    @PUT("events/edit/{id}")
    Call<Void> edit(
            @Path("id") int id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("maxParticipants") RequestBody maxParticipants,
            @Part("privacyType") RequestBody privacyType,
            @Part("startDate") RequestBody startDate,
            @Part List<MultipartBody.Part> images,
            @Part("eventType") RequestBody eventType,
            @Part("locationName") RequestBody locationName,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude
    );
}
