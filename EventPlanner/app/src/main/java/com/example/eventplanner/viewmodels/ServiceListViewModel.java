package com.example.eventplanner.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceListViewModel extends ViewModel {

    private final MutableLiveData<List<Service>> servicesLiveData = new MutableLiveData<List<Service>>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<List<Service>> getServices() {
        return servicesLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    private int currentPage = 1, pageSize = 6, totalPages = 1, minPrice = 0, maxPrice = 0;
    private String name = "", category = "", eventType = "", isAvailable = "";

    public void fetchServices() {
        Call<PagedResponse<Service>> call = ClientUtils.serviceService.searchAndFilter(name, category, eventType, isAvailable, currentPage, pageSize, minPrice, maxPrice);
        call.enqueue(new Callback<PagedResponse<Service>>() {
            @Override
            public void onResponse(Call<PagedResponse<Service>> call, Response<PagedResponse<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<Service> pagedResponse = response.body();
                    servicesLiveData.setValue(pagedResponse.getContent());
                    totalPages = pagedResponse.getTotalPages();
                } else {
                    errorMessage.postValue("Failed to fetch services. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Service>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}