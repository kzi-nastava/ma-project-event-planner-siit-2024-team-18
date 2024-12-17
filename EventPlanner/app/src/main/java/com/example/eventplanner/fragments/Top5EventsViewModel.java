package com.example.eventplanner.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Top5EventsViewModel extends ViewModel {

    private final MutableLiveData<List<EventCard>> top5EventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<EventCard>> getTop5Events() {
        return top5EventsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void fetchTop5Events() {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Collection<EventCard>> call = ClientUtils.eventService.getTop5Events();
        call.enqueue(new Callback<Collection<EventCard>>() {
            @Override
            public void onResponse(Call<Collection<EventCard>> call, Response<Collection<EventCard>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    top5EventsLiveData.setValue(new ArrayList<>(response.body()));
                } else {
                    errorMessage.setValue("Error fetching top 5 events: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Collection<EventCard>> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }
}
