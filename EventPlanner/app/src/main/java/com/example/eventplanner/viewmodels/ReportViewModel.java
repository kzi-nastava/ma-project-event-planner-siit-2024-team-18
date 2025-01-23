package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Report;
import com.example.eventplanner.models.ReportRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<List<ReportRequest>> reportsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public LiveData<List<ReportRequest>> getReports() {
        return reportsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void fetchReports() {
        loading.setValue(true);
        ClientUtils.getReportService(context).getPendingReports().enqueue(new Callback<List<ReportRequest>>() {
            @Override
            public void onResponse(Call<List<ReportRequest>> call, Response<List<ReportRequest>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    reportsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch reports.");
                }
            }

            @Override
            public void onFailure(Call<List<ReportRequest>> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void handleReportAction(int reportId, boolean approve) {
        loading.setValue(true);
        Call<Report> call = approve
                ? ClientUtils.getReportService(context).approveReport(reportId)
                : ClientUtils.getReportService(context).removeReport(reportId);

        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    removeReportById(reportId);
                } else {
                    errorMessage.setValue("Failed to " + (approve ? "approve" : "remove") + " the report.");
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    private void removeReportById(int reportId) {
        List<ReportRequest> currentReports = reportsLiveData.getValue();
        if (currentReports != null) {
            currentReports.removeIf(report -> report.getId() == reportId);
            reportsLiveData.setValue(currentReports);
        }
    }
}
