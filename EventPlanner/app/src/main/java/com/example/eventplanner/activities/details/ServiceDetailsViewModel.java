package com.example.eventplanner.activities.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.ServiceDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsViewModel extends ViewModel {

    private final MutableLiveData<ServiceDetails> serviceDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<ServiceDetails> getServiceDetails() {
        return serviceDetailsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchServiceDetailsById(int serviceId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<ServiceDetails> call = ClientUtils.serviceService.getById(serviceId);
        call.enqueue(new Callback<ServiceDetails>() {
            @Override
            public void onResponse(Call<ServiceDetails> call, Response<ServiceDetails> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    serviceDetailsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching service details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ServiceDetails> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }
}
