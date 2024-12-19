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

    private final MutableLiveData<List<Service>> servicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private int currentPage = 1, pageSize = 6, totalPages = 1, minPrice = 0, maxPrice = 0;
    private String name = "", category = "", eventType = "", isAvailable = "true";

    public LiveData<List<Service>> getServices() {
        return servicesLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchServices() {
        Call<PagedResponse<Service>> call = ClientUtils.serviceService.searchAndFilter(name, category, eventType, isAvailable, currentPage, pageSize, minPrice, maxPrice);
        call.enqueue(new Callback<PagedResponse<Service>>() {
            @Override
            public void onResponse(Call<PagedResponse<Service>> call, Response<PagedResponse<Service>> response) {
                if (response.isSuccessful()) {
                    PagedResponse<Service> pagedResponse = response.body();
                    servicesLiveData.postValue(pagedResponse.getContent());
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

    public void deleteServiceById(int serviceId) {
        Call<Void> call = ClientUtils.serviceService.deleteById(serviceId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchServices();
                } else {
                    errorMessage.postValue("Failed to delete service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Failed to delete service: " + t.getMessage());
            }
        });
    }

    public void setNameFilter(String name) {
        this.name = name;
    }

    public void setCategoryFilter(String category) {
        this.category = category;
    }

    public void setEventTypeFilter(String eventType) {
        this.eventType = eventType;
    }

    public void setPriceRange(int minPrice, int maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public void setAvailabilityFilter(String isAvailable) {
        this.isAvailable = isAvailable;
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
