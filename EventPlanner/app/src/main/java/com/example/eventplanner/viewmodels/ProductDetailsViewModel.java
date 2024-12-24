package com.example.eventplanner.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Product;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewModel extends ViewModel {
    private final MutableLiveData<Product> productDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Product> getProductDetails() {
        return productDetailsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchProductDetailsById(int productId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Product> call = ClientUtils.productService.getProductDetails(productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productDetailsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching product details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }

    public void buyProduct(int productId, int eventId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<ResponseBody> call = ClientUtils.productService.buyProduct(productId, 1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    Log.d("Purchase", "Product purchased successfully!");
                } else {
                    errorMessage.setValue("Error purchasing product: " + response.message());
                    Log.e("Purchase", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e("Purchase", "Failure: " + t.getMessage());
            }
        });
    }

}
