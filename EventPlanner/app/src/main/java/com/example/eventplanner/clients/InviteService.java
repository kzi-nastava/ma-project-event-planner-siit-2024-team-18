package com.example.eventplanner.clients;

import com.example.eventplanner.models.UpdatedInvite;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface InviteService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("invites/accept")
    Call<UpdatedInvite> acceptInvite(@Query("inviteId") int inviteId);
}
