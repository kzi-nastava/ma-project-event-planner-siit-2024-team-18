package com.example.eventplanner.clients;

import com.example.eventplanner.models.NotificationModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface NotificationService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("notifications-all")
    Call<List<NotificationModel>> getAllNotifications();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("toggle-notifications")
    Call<ResponseBody> toggleNotifications();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("{id}")
    Call<NotificationModel> getNotificationById(@Path("id") int id);
}
