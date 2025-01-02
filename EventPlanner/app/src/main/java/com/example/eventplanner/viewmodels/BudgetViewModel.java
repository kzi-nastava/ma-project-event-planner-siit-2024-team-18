package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.BudgetItem;
import com.example.eventplanner.models.ProductDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<BudgetItem>> budgetItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ProductDetails>> solutionDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalBudgetLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<ArrayList<BudgetItem>> getBudgetItems() {
        return budgetItemsLiveData;
    }

    public LiveData<ArrayList<ProductDetails>> getSolutionDetails() {
        return solutionDetailsLiveData;
    }

    public LiveData<Integer> getTotalBudgetLiveData() {
        return totalBudgetLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchBudgetItems() {
        Call<ArrayList<BudgetItem>> call = ClientUtils.getBudgetService(this.context).getAll();
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

    public void fetchSolutionDetails(int eventId) {
        Call<ArrayList<ProductDetails>> call = ClientUtils.getBudgetService(this.context).getSolutionDetails(eventId);
        call.enqueue(new Callback<ArrayList<ProductDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<ProductDetails>> call, Response<ArrayList<ProductDetails>> response) {
                if (response.isSuccessful()) {
                    solutionDetailsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch solutoin details. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ProductDetails>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addBudgetItem(BudgetItem budgetItem) {
        Call<BudgetItem> call = ClientUtils.getBudgetService(this.context).add(1, budgetItem);
        call.enqueue(new Callback<BudgetItem>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchBudgetItems();
                    getTotalBudget(1);
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
        Call<BudgetItem> call = ClientUtils.getBudgetService(this.context).edit(1, budgetItem);
        call.enqueue(new Callback<BudgetItem>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    getTotalBudget(1);
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
        Call<Void> call = ClientUtils.getBudgetService(this.context).deleteById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchBudgetItems();
                    getTotalBudget(1);
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

    public void getTotalBudget(int eventId) {
        Call<Integer> call = ClientUtils.getBudgetService(this.context).getTotalBudget(eventId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    totalBudgetLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch total budget. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                errorMessage.postValue("Failed to fetch total budget: " + t.getMessage());
            }
        });
    }
}
