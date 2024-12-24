package com.example.eventplanner.clients;

import com.example.eventplanner.models.BudgetItem;
import com.example.eventplanner.models.ProductDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BudgetService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("budget")
    Call<ArrayList<BudgetItem>> getAll();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("budget/total/{eventId}")
    Call<Integer> getTotalBudget(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("budget/details/{eventId}")
    Call<ArrayList<ProductDetails>> getDetails(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("budget/create/{eventId}")
    Call<BudgetItem> add(@Path("eventId") int eventId, @Body BudgetItem budgetItem);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("budget/edit/{eventId}")
    Call<BudgetItem> edit(@Path("eventId") int eventId, @Body BudgetItem budgetItem);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("budget/delete/{id}")
    Call<Void> deleteById(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("budget/purchase-product/{productId}")
    Call<Void> purchaseProduct(@Path("productId") int productId);

}
