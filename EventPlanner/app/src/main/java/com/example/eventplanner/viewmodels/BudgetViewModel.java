package com.example.eventplanner.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.BudgetItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<BudgetItem>> budgetItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    public LiveData<ArrayList<BudgetItem>> getBudgetItems() {
        return budgetItemsLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getSuccess() {
        return success;
    }
    public void fetchBudgetItems() {
        Call<ArrayList<BudgetItem>> call = ClientUtils.budgetService.getAll();
        call.enqueue(new Callback<ArrayList<BudgetItem>>() {
            @Override
            public void onResponse(Call<ArrayList<BudgetItem>> call, Response<ArrayList<BudgetItem>> response) {
                if (response.isSuccessful()) {
                    budgetItemsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch budget items. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BudgetItem>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addBudgetItem(BudgetItem budgetItem) {
        Call<BudgetItem> call = ClientUtils.budgetService.add(1, budgetItem);
        call.enqueue(new Callback<BudgetItem>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchBudgetItems();
                } else {
                    errorMessage.postValue("Failed to add Budget Item. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BudgetItem> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editBudgetItem(int id, BudgetItem budgetItem) {
        Call<BudgetItem> call = ClientUtils.budgetService.edit(1, budgetItem);
        call.enqueue(new Callback<BudgetItem>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit budget item. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BudgetItem> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteBudgetItemById(int id) {
        Call<Void> call = ClientUtils.budgetService.deleteById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchBudgetItems();
                } else {
                    errorMessage.postValue("Failed to delete budget item. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Failed to delete budget item: " + t.getMessage());
            }
        });
    }
}
