package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<EventActivity>> activitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<ArrayList<EventActivity>> getActivities() {
        return activitiesLiveData;
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

    public void fetchActivities(int eventId) {
        Call<ArrayList<EventActivity>> call = ClientUtils.getActivityService(this.context).getAll(eventId);
        call.enqueue(new Callback<ArrayList<EventActivity>>() {
            @Override
            public void onResponse(Call<ArrayList<EventActivity>> call, Response<ArrayList<EventActivity>> response) {
                if (response.isSuccessful()) {
                    activitiesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch activities. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventActivity>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addActivity(int eventId, EventActivity activity) {
        Call<EventActivity> call = ClientUtils.getActivityService(this.context).add(eventId, activity);
        call.enqueue(new Callback<EventActivity>() {
            @Override
            public void onResponse(Call<EventActivity> call, Response<EventActivity> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchActivities(eventId);
                } else {
                    errorMessage.postValue("Failed to add activity. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<EventActivity> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editActivity(int eventId, int id, EventActivity activity) {
        Call<Void> call = ClientUtils.getActivityService(this.context).edit(id, activity);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchActivities(eventId);
                } else {
                    errorMessage.postValue("Failed to edit activity. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteActivityById(int eventId, int id) {
        Call<Void> call = ClientUtils.getActivityService(this.context).deleteById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchActivities(eventId);
                } else {
                    errorMessage.postValue("Failed to delete activity. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Failed to delete activity: " + t.getMessage());
            }
        });
    }

    public EventActivity getLastActivity() {
        ArrayList<EventActivity> list = activitiesLiveData.getValue();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }
}
