package com.example.eventplanner.clients;

import com.example.eventplanner.models.Service;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
    @GET("services/{id}")
    Call<Service> getById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("services/")
    Call<Service> add(@Body Service service);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("services/{id}")
    Call<ResponseBody> deleteById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("services/")
    Call<Service> edit(@Body Service service);
}
