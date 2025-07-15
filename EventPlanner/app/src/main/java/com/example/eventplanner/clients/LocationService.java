package com.example.eventplanner.clients;

import com.example.eventplanner.models.LocationAutocompleteResponseDTO;
import com.example.eventplanner.models.LocationDetailResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LocationService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("locations/autocomplete")
    Call<List<LocationAutocompleteResponseDTO>> getAutocompleteLocations(@Query("query") String query);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("locations/details")
    Call<LocationDetailResponseDTO> getLocationDetails(@Query("query") String query);
}
