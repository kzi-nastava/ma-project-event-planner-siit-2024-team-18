package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.models.Service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricelistViewModel extends ViewModel {
    private Context context;
    private final MutableLiveData<ArrayList<Service>> servicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Product>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }


    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public LiveData<ArrayList<Service>> getServices() {
        return servicesLiveData;
    }

    public LiveData<ArrayList<Product>> getProducts() {
        return productsLiveData;
    }

    public void fetchPricelistItems() {
        getServicesBackend();
        getProductsBackend();
    }

    public <T> void editPricelistItem(int id, T solution) {
        Call<T> call = ClientUtils.getPricelistService(this.context).edit(id, solution);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to solution. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void getProductsBackend() {
        Call<ArrayList<Product>> call2 = ClientUtils.getProductService(this.context).getAllByCreator();
        call2.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call2, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    productsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch products. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call2, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });

    }

    public void getServicesBackend() {
        Call<ArrayList<Service>> call1 = ClientUtils.getServiceService(this.context).getAllByCreator();
        call1.enqueue(new Callback<ArrayList<Service>>() {
            @Override
            public void onResponse(Call<ArrayList<Service>> call1, Response<ArrayList<Service>> response) {
                if (response.isSuccessful()) {
                    servicesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch services. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Service>> call1, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
