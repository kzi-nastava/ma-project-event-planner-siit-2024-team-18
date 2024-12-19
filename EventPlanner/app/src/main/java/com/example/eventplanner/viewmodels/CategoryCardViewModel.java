package com.example.eventplanner.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Category;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryCardViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<ArrayList<Category>> getCategories() {
        return categoriesLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchCategories() {
        Call<ArrayList<Category>> call = ClientUtils.categoryService.getAll();
        call.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if (response.isSuccessful()) {
                    categoriesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch Categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Category>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
