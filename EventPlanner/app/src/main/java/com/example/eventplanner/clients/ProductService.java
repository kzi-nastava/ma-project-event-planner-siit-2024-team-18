package com.example.eventplanner.clients;

import com.example.eventplanner.models.Product;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    @POST("products/comment/{id}")
    Call<ResponseBody> commentProduct(@Body String commentContent, @Path("id") int id);

}
