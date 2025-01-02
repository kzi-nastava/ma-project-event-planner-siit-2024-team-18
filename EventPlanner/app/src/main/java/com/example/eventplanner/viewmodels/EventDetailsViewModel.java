package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Event;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Event>> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Event> getEventDetails() {
        return eventDetailsLiveData;
    }

    public LiveData<ArrayList<Event>> getEvents() {
        return allEventsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchEvents() {
        Call<ArrayList<Event>> call = ClientUtils.getEventService(this.context).getAll();
        call.enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                if (response.isSuccessful()) {
                    allEventsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch events. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void fetchEventDetailsById(int eventId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Event> call = ClientUtils.getEventService(this.context).getEventDetailsById(eventId);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    eventDetailsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching event details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }
}
