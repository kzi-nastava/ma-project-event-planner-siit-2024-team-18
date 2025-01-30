package com.example.eventplanner.clients;

import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.Message;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommunicationService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chat/chats/{userId}")
    Call<ArrayList<Chat>> getChats(@Path("userId") int userId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chat/messages/{chatId}")
    Call<ArrayList<Message>> getMessages(@Path("chatId") int chatId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("chat/messages/update")
    Call<Void> updateMessages(@Body ArrayList<Message> messages);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chat/messages/last/{chatId}")
    Call<Message> getLastMessage(@Path("chatId") int chatId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chat/chat/create/product/{productId}")
    Call<Integer> createChatProduct(@Path("productId") int productId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chat/chat/create/service/{serviceId}")
    Call<Integer> createChatService(@Path("serviceId") int serviceId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("chat/chat/create/event/{eventId}")
    Call<Integer> createChatEvent(@Path("eventId") int eventId);
}
