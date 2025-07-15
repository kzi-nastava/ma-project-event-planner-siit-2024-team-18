package com.example.eventplanner.clients;

import com.example.eventplanner.models.Grade;
import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.Product;

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

public interface ProductService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/details/{id}")
    Call<Product> getProductDetails(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/grade/{id}")
    Call<Grade> getProductGrade(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/reviews/{id}")
    Call<Integer> getProductReviews(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/creator")
    Call<ArrayList<Product>> getAllByCreator();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("budget/purchase-product/{productId}")
    Call<ResponseBody> buyProduct(@Path("productId") int productId, @Query("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("products/grade/{productId}")
    Call<ResponseBody> rateProduct(@Path("productId") int productId, @Body Grade grade);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/search")
    Call<PagedResponse<Product>> searchAndFilter(
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
    @DELETE("products/delete/{id}")
    Call<Void> deleteById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android"
    })
    @Multipart
    @POST("products/create")
    Call<Product> add(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("eventTypes") RequestBody eventTypes,
            @Part("location") RequestBody location,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("price") RequestBody price,
            @Part("discount") RequestBody discount,
            @Part("visible") RequestBody visible,
            @Part("available") RequestBody available,
            @Part List<MultipartBody.Part> images
    );

    @Headers({
            "User-Agent: Mobile-Android"
    })
    @Multipart
    @PUT("products/edit/{id}")
    Call<Product> edit(
            @Path("id") int id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("eventTypes") RequestBody eventTypes,
            @Part("location") RequestBody location,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("price") RequestBody price,
            @Part("discount") RequestBody discount,
            @Part("visible") RequestBody visible,
            @Part("available") RequestBody available,
            @Part List<MultipartBody.Part> images
    );
}
