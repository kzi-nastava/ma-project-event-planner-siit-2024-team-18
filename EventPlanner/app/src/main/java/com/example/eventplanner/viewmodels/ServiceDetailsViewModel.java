package com.example.eventplanner.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsViewModel extends ViewModel {
    private final MutableLiveData<Service> serviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Service> getService() {
        return serviceLiveData;
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchServiceById(int ServiceId) {
        Call<Service> call = ClientUtils.serviceService.getById(ServiceId);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    serviceLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch Service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteServiceById(int ServiceId) {
        Call<ResponseBody> call = ClientUtils.serviceService.deleteById(ServiceId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    deleteSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Failed to delete Service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
