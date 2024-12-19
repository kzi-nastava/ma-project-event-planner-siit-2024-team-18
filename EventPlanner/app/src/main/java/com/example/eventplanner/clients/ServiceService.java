package com.example.eventplanner.clients;

import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.Service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services")
    Call<ArrayList<Service>> getAll();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/search")
    Call<PagedResponse<Service>> searchAndFilter(
            @Query("name") String name,
            @Query("category") String category,
            @Query("eventType") String eventType,
            @Query("isAvailable") String isAvailable,
            @Query("page") int page,
            @Query("size") int size,
            @Query("minPrice") double minPrice,
            @Query("maxPrice") double maxPrice
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/details/{id}")
    Call<Service> getDetailsById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/{id}")
    Call<Service> getById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("services/create")
    Call<Service> add(@Body Service service);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("services/delete/{id}")
    Call<Void> deleteById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("services/edit/{id}")
    Call<Service> edit(@Path("id") int id, @Body Service service);
}
