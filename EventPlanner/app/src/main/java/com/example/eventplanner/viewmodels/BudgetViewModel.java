package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.BudgetItem;
import com.example.eventplanner.models.ProductDetails;
import com.example.eventplanner.models.SolutionDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<BudgetItem>> budgetItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<SolutionDetails>> solutionDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProductLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalBudgetLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<ArrayList<BudgetItem>> getBudgetItems() {
        return budgetItemsLiveData;
    }

    public LiveData<ArrayList<SolutionDetails>> getSolutionDetails() {
        return solutionDetailsLiveData;
    }
    public LiveData<Boolean> isProduct() {
        return isProductLiveData;
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

    public void fetchBudgetItems(int eventId) {
        Call<ArrayList<BudgetItem>> call = ClientUtils.getBudgetService(this.context).getAll(eventId);
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
        Call<ArrayList<SolutionDetails>> call = ClientUtils.getBudgetService(this.context).getSolutionDetails(eventId);
        call.enqueue(new Callback<ArrayList<SolutionDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<SolutionDetails>> call, Response<ArrayList<SolutionDetails>> response) {
                if (response.isSuccessful()) {
                    solutionDetailsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch solution details. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SolutionDetails>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void fetchIsProduct(int solutionId) {
        Call<Boolean> call = ClientUtils.getBudgetService(this.context).isProduct(solutionId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    isProductLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch solution details. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addBudgetItem(int eventId, BudgetItem budgetItem) {
        Call<BudgetItem> call = ClientUtils.getBudgetService(this.context).add(eventId, budgetItem);
        call.enqueue(new Callback<BudgetItem>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchBudgetItems(eventId);
                    getTotalBudget(eventId);
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

    public void editBudgetItem(int eventId, int id, BudgetItem budgetItem) {
        Call<Void> call = ClientUtils.getBudgetService(this.context).edit(id, budgetItem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchBudgetItems(eventId);
                    getTotalBudget(eventId);
                } else {
                    errorMessage.postValue("Failed to edit budget item. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteBudgetItemById(int eventId, int id) {
        Call<Void> call = ClientUtils.getBudgetService(this.context).deleteById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchBudgetItems(eventId);
                    getTotalBudget(eventId);
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
