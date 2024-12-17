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
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    public LiveData<Boolean> getSuccess() {
        return success;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setService(Service service) {
        serviceLiveData.setValue(service);
    }

    public LiveData<Service> getService() {return serviceLiveData;}

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

    public void addNewService(Service newService) {
        Call<Service> call = ClientUtils.serviceService.add(newService);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to add service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editService(int id, Service editService) {
        Call<Service> call = ClientUtils.serviceService.edit(id, editService);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
