package com.example.eventplanner.activities.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Event;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsViewModel extends ViewModel {

    private final MutableLiveData<Event> eventDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Event> getEventDetails() {
        return eventDetailsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchEventDetailsById(int eventId) {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Event> call = ClientUtils.eventService.getEventDetailsById(eventId);
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
