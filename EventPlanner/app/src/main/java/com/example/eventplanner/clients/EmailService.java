package com.example.eventplanner.clients;


import com.example.eventplanner.models.EventInvitation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EmailService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("emails/send-invitation/{eventId}")
    Call<EventInvitation> sendEventInvitations(
            @Path("eventId") int eventId,
            @Body List<String> emails
    );
}
