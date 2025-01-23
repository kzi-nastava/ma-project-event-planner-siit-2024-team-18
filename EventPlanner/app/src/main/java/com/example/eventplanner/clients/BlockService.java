package com.example.eventplanner.clients;

import com.example.eventplanner.models.Block;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BlockService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("blocks/{blockedId}")
    Call<Block> getBlock(@Path("blockedId") int blockedId);

    @POST("blocks/block-user")
    Call<Block> blockOtherUser(@Body Block block);

    @PUT("blocks/unblock-user")
    Call<Block> unblockOtherUser(@Body Block block);
}
