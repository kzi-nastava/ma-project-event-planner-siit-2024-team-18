package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private int currentPage = 1, pageSize = 6, totalPages = 1, minPrice = 0, maxPrice = 0;
    private String name = "", category = "", eventType = "", isAvailable = "true";

    public LiveData<List<Product>> getProducts() {
        return productsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchProducts() {
        Call<PagedResponse<Product>> call = ClientUtils.getProductService(this.context).searchAndFilter(name, category, eventType, isAvailable, currentPage, pageSize, minPrice, maxPrice);
        call.enqueue(new Callback<PagedResponse<Product>>() {
            @Override
            public void onResponse(Call<PagedResponse<Product>> call, Response<PagedResponse<Product>> response) {
                if (response.isSuccessful()) {
                    PagedResponse<Product> pagedResponse = response.body();
                    productsLiveData.postValue(pagedResponse.getContent());
                    totalPages = pagedResponse.getTotalPages();
                } else {
                    errorMessage.postValue("Failed to fetch products. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Product>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteProductById(int productId) {
        Call<Void> call = ClientUtils.getProductService(this.context).deleteById(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchProducts();
                } else {
                    errorMessage.postValue("Failed to delete product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Failed to delete product: " + t.getMessage());
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
