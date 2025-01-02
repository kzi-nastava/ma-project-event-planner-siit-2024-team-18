package com.example.eventplanner.clients;

import com.example.eventplanner.models.AuthResponse;
import com.example.eventplanner.models.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {
    @Headers({"Content-Type: application/json"})
    @POST("/login")
    Call<AuthResponse> login(@Body Login authRequest);
}
