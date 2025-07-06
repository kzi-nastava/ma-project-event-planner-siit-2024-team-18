package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {
    private Context context;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> loggedUserLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<User>> usersLiveData = new MutableLiveData<>();

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getLoggedUser() {
        return loggedUserLiveData;
    }
    public LiveData<ArrayList<User>> getAllUsers() {
        return usersLiveData;
    }

    public void fetchLoggedUser() {
        Call<User> call = ClientUtils.getUserService(this.context).getLoggedUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    loggedUserLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch logged user. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }


    public void fetchAllUsers() {
        Call<ArrayList<User>> call = ClientUtils.getUserService(this.context).getAllUsers();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    usersLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch users. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
