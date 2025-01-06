package com.example.eventplanner.clients;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PricelistService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("pricelist/edit/{id}")
    <T> Call<T> edit(@Path("id") int id, @Body T solution);
}
