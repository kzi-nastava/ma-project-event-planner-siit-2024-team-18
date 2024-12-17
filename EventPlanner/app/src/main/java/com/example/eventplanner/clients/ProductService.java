package com.example.eventplanner.clients;

import com.example.eventplanner.models.ProductDetailsDTO;

import java.util.ArrayList;
import java.util.Collection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("api/products/details/{id}")
    Call<ProductDetailsDTO> getProductDetails(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("api/products/comment/{id}")
    Call<ResponseBody> commentProduct(@Body String commentContent, @Path("id") int id);

}
