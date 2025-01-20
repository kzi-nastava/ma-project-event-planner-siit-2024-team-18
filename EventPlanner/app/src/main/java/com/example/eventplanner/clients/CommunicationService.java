package com.example.eventplanner.clients;

import com.example.eventplanner.models.Chat;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface CommunicationService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chats/{userId}")
    Call<ArrayList<Chat>> getChats(@Path("userId") int userId);
}
