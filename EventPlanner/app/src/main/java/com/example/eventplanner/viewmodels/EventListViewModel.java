package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListViewModel extends ViewModel {

    private final MutableLiveData<List<EventCard>> eventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<EventCard>> getEvents() {
        return eventsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchEvents() {
        Call<List<EventCard>> call = ClientUtils.getEventService(this.context).getByEventOrganizer();
        call.enqueue(new Callback<List<EventCard>>() {
            @Override
            public void onResponse(Call<List<EventCard>> call, Response<List<EventCard>> response) {
                if (response.isSuccessful()) {
                    List<EventCard> events = response.body();
                    eventsLiveData.postValue(events);
                } else {
                    errorMessage.postValue("Failed to fetch events. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EventCard>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteEventById(int eventId) {
        Call<Void> call = ClientUtils.getEventService(this.context).deleteById(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchEvents();
                } else {
                    errorMessage.postValue("Failed to delete event. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Failed to delete event: " + t.getMessage());
            }
        });
    }
}
