package com.example.eventplanner.clients;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RegistrationRequestService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("registration-requests/accept")
    Call<Void> activateAccount(@Query("id") int id);
}
