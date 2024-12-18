package com.example.eventplanner.activities.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.ProductDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewModel extends ViewModel {

    private final MutableLiveData<ProductDetails> productDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<ProductDetails> getProductDetails() {
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

        Call<ProductDetails> call = ClientUtils.productService.getProductDetails(productId);
        call.enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(Call<ProductDetails> call, Response<ProductDetails> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productDetailsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching product details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProductDetails> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }
}
