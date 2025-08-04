package com.example.eventplanner.clients;

import com.example.eventplanner.models.Product;
import com.example.eventplanner.models.Service;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PricelistService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("pricelist/edit/{id}")
    Call<Service> editService(@Path("id") int id, @Body Service solution);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("pricelist/edit/{id}")
    Call<Product> editProduct(@Path("id") int id, @Body Product solution);

    @POST("/api/pdf/{type}")
    Call<ResponseBody> generatePDF(@Path("type") String type, @Body List<Map<String, Object>> data);
}
