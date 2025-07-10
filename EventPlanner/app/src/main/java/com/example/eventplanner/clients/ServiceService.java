package com.example.eventplanner.clients;

import com.example.eventplanner.models.Grade;
import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.Service;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @GET("services/creator")
    Call<ArrayList<Service>> getAllByCreator();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/grade/{id}")
    Call<Grade> getServiceGrade(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/reviews/{id}")
    Call<Integer> getServiceReviews(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("services/grade/{serviceId}")
    Call<ResponseBody> rateService(@Path("serviceId") int serviceId, @Body Grade grade);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/search")
    Call<PagedResponse<Service>> searchAndFilter(
            @Query("name") String name,
            @Query("category") String category,
            @Query("eventType") String eventType,
            @Query("isAvailable") String isAvailable,
            @Query("page") int page,
            @Query("size") int size,
            @Query("minPrice") double minPrice,
            @Query("maxPrice") double maxPrice
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/details/{id}")
    Call<Service> getDetailsById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("services/details/{id}")
    Call<Service> getById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android"
    })
    @Multipart
    @POST("services/create")
    Call<Service> add(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("specifics") RequestBody specifics,
            @Part("category") RequestBody category,
            @Part("eventTypes") RequestBody eventTypes,
            @Part("location") RequestBody location,
            @Part("reservationDeadline") RequestBody reservationDeadline,
            @Part("cancellationDeadline") RequestBody cancellationDeadline,
            @Part("price") RequestBody price,
            @Part("discount") RequestBody discount,
            @Part("duration") RequestBody duration,
            @Part("minEngagement") RequestBody minEngagement,
            @Part("maxEngagement") RequestBody maxEngagement,
            @Part("visible") RequestBody visible,
            @Part("available") RequestBody available,
            @Part("reservationType") RequestBody reservationType,
            @Part("workingHoursStart") RequestBody workingHoursStart,
            @Part("workingHoursEnd") RequestBody workingHoursEnd,
            @Part List<MultipartBody.Part> images
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("services/delete/{id}")
    Call<Void> deleteById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android"
    })
    @Multipart
    @PUT("services/edit/{id}")
    Call<Service> edit(
            @Path("id") int id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("specifics") RequestBody specifics,
            @Part("category") RequestBody category,
            @Part("eventTypes") RequestBody eventTypes,
            @Part("location") RequestBody location,
            @Part("reservationDeadline") RequestBody reservationDeadline,
            @Part("cancellationDeadline") RequestBody cancellationDeadline,
            @Part("price") RequestBody price,
            @Part("discount") RequestBody discount,
            @Part("duration") RequestBody duration,
            @Part("minEngagement") RequestBody minEngagement,
            @Part("maxEngagement") RequestBody maxEngagement,
            @Part("visible") RequestBody visible,
            @Part("available") RequestBody available,
            @Part("reservationType") RequestBody reservationType,
            @Part("workingHoursStart") RequestBody workingHoursStart,
            @Part("workingHoursEnd") RequestBody workingHoursEnd,
            @Part List<MultipartBody.Part> images
    );
}
