package com.example.eventplanner.clients;

import com.example.eventplanner.models.Grade;
import com.example.eventplanner.models.Product;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
    @POST("products/comment/{id}")
    Call<ResponseBody> commentProduct(@Body String commentContent, @Path("id") int id);

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
}
