package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.Comment;
import com.example.eventplanner.models.Grade;
import com.example.eventplanner.models.Product;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    private final MutableLiveData<ArrayList<Comment>> commentsLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getSuccess() {
        return success;
    }
    public LiveData<Boolean> getProductPurchased() {
        return productPurchased;
    }
    public LiveData<Product> getProductDetails() {
        return productDetailsLiveData;
    }
    public LiveData<ArrayList<Comment>> getComments() {
        return commentsLiveData;
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

    public void addNewProduct(RequestBody nameBody, RequestBody descriptionBody, RequestBody categoryBody, RequestBody eventTypesBody, RequestBody locationBody, RequestBody cityBody, RequestBody countryBody, RequestBody latitudeBody, RequestBody longitudeBody, RequestBody priceBody, RequestBody discountBody, RequestBody visibleBody, RequestBody availableBody, List<MultipartBody.Part> imageParts) {
        Call<Product> call = ClientUtils.getProductService(this.context).add(nameBody, descriptionBody, categoryBody, eventTypesBody, locationBody, cityBody, countryBody, latitudeBody, longitudeBody, priceBody, discountBody, visibleBody, availableBody, imageParts);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to add product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editProduct(int id, RequestBody nameBody, RequestBody descriptionBody, RequestBody categoryBody, RequestBody eventTypesBody, RequestBody locationBody, RequestBody cityBody, RequestBody countryBody, RequestBody latitudeBody, RequestBody longitudeBody, RequestBody priceBody, RequestBody discountBody, RequestBody visibleBody, RequestBody availableBody, List<MultipartBody.Part> imageParts) {
        Call<Product> call = ClientUtils.getProductService(this.context).edit(id, nameBody, descriptionBody, categoryBody, eventTypesBody, locationBody, cityBody, countryBody, latitudeBody, longitudeBody, priceBody, discountBody, visibleBody, availableBody, imageParts);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
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

    public void fetchComments(int solutionId) {
        Call<ArrayList<Comment>> call = ClientUtils.getProductService(this.context).getComments(solutionId);
        call.enqueue(new Callback<ArrayList<Comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                if (response.isSuccessful()) {
                    commentsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch Comments. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
