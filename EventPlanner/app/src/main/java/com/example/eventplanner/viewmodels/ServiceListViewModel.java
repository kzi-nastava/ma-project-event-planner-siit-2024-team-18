package com.example.eventplanner.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.adapters.ServiceListAdapter;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceListViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Service>> servicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<ArrayList<Service>> getServices() {
        return servicesLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchServices() {
        Call<ArrayList<Service>> call = ClientUtils.serviceService.getAll();
        call.enqueue(new Callback<ArrayList<Service>>() {
            @Override
            public void onResponse(Call<ArrayList<Service>> call, Response<ArrayList<Service>> response) {
                if (response.isSuccessful()) {
                    servicesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch services. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Service>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}