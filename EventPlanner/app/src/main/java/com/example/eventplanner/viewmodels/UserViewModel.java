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
    private final MutableLiveData<Boolean> joinSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> leaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addFavSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> removeFavSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLiked = new MutableLiveData<>();
    public LiveData<Boolean> getRemoveFavSuccess() {
        return removeFavSuccess;
    }
    public LiveData<Boolean> getAddFavSuccess() {
        return addFavSuccess;
    }
    public LiveData<Boolean> getLeaveSuccess() {
        return leaveSuccess;
    }
    public LiveData<Boolean> getJoinSuccess() {
        return joinSuccess;
    }
    public LiveData<Boolean> isLiked() {
        return isLiked;
    }

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

    public void addToFavourites(int eventId) {
        Call<Void> call = ClientUtils.getUserService(context).addEventToFavourites(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to add to favourites. Code: " + response.code());
                } else {
                    addFavSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void removeFromFavourites(int eventId) {
        Call<Void> call = ClientUtils.getUserService(context).removeEventFromFavourites(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to remove from favourites. Code: " + response.code());
                } else {
                    removeFavSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addToFavouritesSolution(int solutionId) {
        Call<Void> call = ClientUtils.getUserService(context).addToFavouritesSolution(solutionId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to add to favourites. Code: " + response.code());
                } else {
                    addFavSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void removeFromFavouritesSolution(int solutionId) {
        Call<Void> call = ClientUtils.getUserService(context).removeFromFavouritesSolution(solutionId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to remove from favourites. Code: " + response.code());
                } else {
                    removeFavSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void fetchIsLiked(int solutionId) {
        Call<Boolean> call = ClientUtils.getUserService(context).isLiked(solutionId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to get status. Code: " + response.code());
                } else {
                    isLiked.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void joinEvent(int eventId) {
        Call<Void> call = ClientUtils.getUserService(context).joinEvent(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("There is no space available on this event.");
                } else {
                    joinSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void leaveEvent(int eventId) {
        Call<Void> call = ClientUtils.getUserService(context).leaveEvent(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to leave event. Code: " + response.code());
                } else {
                    leaveSuccess.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
