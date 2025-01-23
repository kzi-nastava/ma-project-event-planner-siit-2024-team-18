package com.example.eventplanner.clients;

import com.example.eventplanner.models.Report;
import com.example.eventplanner.models.ReportRequest;
import com.example.eventplanner.models.SuspensionDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("reports")
    Call<Report> createReport(@Body Report report);

    @GET("reports")
    Call<List<ReportRequest>> getPendingReports();

    @PUT("reports/{reportId}/approve")
    Call<Report> approveReport(@Path("reportId") int reportId);

    @PUT("reports/{reportId}/remove")
    Call<Report> removeReport(@Path("reportId") int reportId);

    @GET("reports/suspensions/{email}")
    Call<List<SuspensionDetails>> getSuspensionDetails(@Path("email") String email);
}
