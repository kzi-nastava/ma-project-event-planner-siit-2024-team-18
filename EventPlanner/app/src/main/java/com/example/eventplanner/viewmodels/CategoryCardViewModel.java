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
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    public LiveData<ArrayList<Category>> getCategories() {
        return categoriesLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getSuccess() {
        return success;
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

    public void addCategory(Category category) {
        Call<Category> call = ClientUtils.categoryService.add(category);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to add category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editCategory(int id, Category category) {
        Call<Category> call = ClientUtils.categoryService.edit(id, category);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteCategoryById(int id) {
        Call<Void> call = ClientUtils.categoryService.deleteById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchCategories();
                } else {
                    errorMessage.postValue("Failed to delete category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Failed to delete category: " + t.getMessage());
            }
        });
    }
}
