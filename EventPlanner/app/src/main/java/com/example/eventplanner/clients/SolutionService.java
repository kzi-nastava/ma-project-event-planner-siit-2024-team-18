package com.example.eventplanner.clients;

import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.SolutionCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SolutionService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("api/solutions/top-solutions")
    Call<List<SolutionCard>> getTopSolutions();

    @GET("api/solutions/{id}")
    Call<SolutionCard> getSolutionById(@Path("id") int id);

    @GET("api/solutions/isProduct/{id}")
    Call<Boolean> isSolutionProduct(@Path("id") int id);

    @GET("api/solutions")
    Call<PagedResponse<SolutionCard>> searchAndFilterSolutions(
            @Query("keyword") String keyword,
            @Query("city") String city,
            @Query("isProductOnly") Boolean isProductOnly,
            @Query("name") String name,
            @Query("description") String description,
            @Query("price") Double price,
            @Query("discount") Double discount,
            @Query("category") String category,
            @Query("providerFirstName") String providerFirstName,
            @Query("country") String country,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") String sortDirection
    );
}
