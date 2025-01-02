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
    public LiveData<ArrayList<EventType>> getEventTypes() {
        return eventTypesLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
}
