package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeCardViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<EventType>> eventTypesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<ArrayList<EventType>> getEventTypes() {
        return eventTypesLiveData;
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

    public void fetchEventTypes() {
        Call<ArrayList<EventType>> call = ClientUtils.getEventTypeService(this.context).getAll();
        call.enqueue(new Callback<ArrayList<EventType>>() {
            @Override
            public void onResponse(Call<ArrayList<EventType>> call, Response<ArrayList<EventType>> response) {
                if (response.isSuccessful()) {
                    eventTypesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch Event Types. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventType>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void createEventType(EventType eventType) {
        Call<Void> call = ClientUtils.getEventTypeService(this.context).create(eventType);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchEventTypes();
                } else if (response.code() == 400) {
                    errorMessage.postValue("Event type with same name already exists.");
                } else {
                    errorMessage.postValue("Failed to add category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void updateEventType(int id, EventType eventType) {
        Call<Void> call = ClientUtils.getEventTypeService(this.context).update(id, eventType);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchEventTypes();
                } else {
                    errorMessage.postValue("Failed to edit category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteEventType(int id) {
        Call<Void> call = ClientUtils.getEventTypeService(this.context).delete(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchEventTypes();
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
