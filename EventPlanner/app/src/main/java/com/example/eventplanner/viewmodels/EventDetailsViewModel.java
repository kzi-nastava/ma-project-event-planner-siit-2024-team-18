package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.Product;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Event>> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<EventCard>> allEventsByCreatorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<Event> getEventDetails() {
        return eventDetailsLiveData;
    }

    public LiveData<ArrayList<EventCard>> getEventsByCreator() {
        return allEventsByCreatorLiveData;
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

    public void fetchEventsByCreator() {
        Call<ArrayList<EventCard>> call = ClientUtils.getEventService(this.context).getAllByCreator();
        call.enqueue(new Callback<ArrayList<EventCard>>() {
            @Override
            public void onResponse(Call<ArrayList<EventCard>> call, Response<ArrayList<EventCard>> response) {
                if (response.isSuccessful()) {
                    allEventsByCreatorLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch events. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventCard>> call, Throwable t) {
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
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        eventDetailsLiveData.setValue(response.body());
                    } else {
                        // Handle empty response
                        errorMessage.setValue("Event not found or no data available.");
                    }
                } else {
                    errorMessage.setValue("Error fetching event details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                loading.setValue(false);
                Log.e("EventDetailsActivity", "Failed to fetch event by id", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public EventCard getEventByName(String eventName) {
        for (EventCard eventCard : allEventsByCreatorLiveData.getValue()) {
            if (eventCard.getName().equals(eventName)) {
                return eventCard;
            }
        }
        return null;
    }

    public void addNewEvent(RequestBody nameBody, RequestBody descriptionBody, RequestBody maxParticipantsBody, RequestBody eventTypeBody, RequestBody locationNameBody, RequestBody cityBody, RequestBody countryBody, RequestBody latitudeBody, RequestBody longitudeBody, RequestBody privacyTypeBody, RequestBody dateTimeBody, List<MultipartBody.Part> imageParts) {
        Call<Void> call = ClientUtils.getEventService(this.context).add(nameBody, descriptionBody, maxParticipantsBody, privacyTypeBody, dateTimeBody, imageParts, eventTypeBody, locationNameBody, cityBody, countryBody, latitudeBody, longitudeBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to add event. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editEvent(int id, RequestBody nameBody, RequestBody descriptionBody, RequestBody maxParticipantsBody, RequestBody eventTypeBody, RequestBody locationNameBody, RequestBody cityBody, RequestBody countryBody, RequestBody latitudeBody, RequestBody longitudeBody, RequestBody privacyTypeBody, RequestBody dateTimeBody, List<MultipartBody.Part> imageParts) {
        Call<Void> call = ClientUtils.getEventService(this.context).edit(id, nameBody, descriptionBody, maxParticipantsBody, privacyTypeBody, dateTimeBody, imageParts, eventTypeBody, locationNameBody, cityBody, countryBody, latitudeBody, longitudeBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit event. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
