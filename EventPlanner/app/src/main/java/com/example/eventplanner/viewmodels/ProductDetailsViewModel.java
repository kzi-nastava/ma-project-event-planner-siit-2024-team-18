package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Grade;
import com.example.eventplanner.models.Product;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewModel extends ViewModel {
    private final MutableLiveData<Product> productDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Grade> productGradeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> productReviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> productPurchased = new MutableLiveData<>();
    public LiveData<Boolean> getProductPurchased() {
        return productPurchased;
    }
    public LiveData<Product> getProductDetails() {
        return productDetailsLiveData;
    }

    public LiveData<Grade> getProductGrade() {
        return productGradeLiveData;
    }

    public LiveData<Integer> getProductReviews() {
        return productReviewsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchProductDetailsById(int productId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Product> call = ClientUtils.getProductService(this.context).getProductDetails(productId);
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
                Log.e("ProductDetailsActivity", "Failed to fetch product by id", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchProductRating(int productId) {
        fetchProductGrade(productId);
        fetchProductReviews(productId);
    }

    private void fetchProductGrade(int productId) {
        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Grade> call = ClientUtils.getProductService(this.context).getProductGrade(productId);
        call.enqueue(new Callback<Grade>() {
            @Override
            public void onResponse(Call<Grade> call, Response<Grade> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productGradeLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching product grade: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Grade> call, Throwable t) {
                loading.setValue(false);
                Log.e("ProductDetailsActivity", "Failed to fetch product grade", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private void fetchProductReviews(int productId) {
        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Integer> call = ClientUtils.getProductService(this.context).getProductReviews(productId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productReviewsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching product reviews: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                loading.setValue(false);
                Log.e("ProductDetailsActivity", "Failed to fetch product reviews", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void buyProduct(int productId, int eventId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;
        loading.setValue(true);
        errorMessage.setValue(null);
        productPurchased.setValue(null);

        Call<ResponseBody> call = ClientUtils.getProductService(this.context).buyProduct(productId, eventId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    productPurchased.setValue(true);
                    Log.d("Purchase", "Product purchased successfully!");
                } else {
                    productPurchased.setValue(false);
                    errorMessage.setValue("Error purchasing product: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.setValue(false);
                productPurchased.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void rateProduct(int productId, int value, String comment) {
        loading.setValue(true);
        errorMessage.setValue(null);

        Grade grade = new Grade(value, comment);
        Call<ResponseBody> call = ClientUtils.getProductService(this.context).rateProduct(productId, grade);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    fetchProductRating(productId);
                    Log.d("Rating", "Product rated successfully!");
                } else {
                    errorMessage.setValue("Error rating product: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
