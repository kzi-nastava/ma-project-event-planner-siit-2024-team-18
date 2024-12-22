package com.example.eventplanner.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.Reservation;
import com.example.eventplanner.models.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReservationViewModel extends ViewModel {

    private final MutableLiveData<Collection<Reservation>> reservations = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedEventId = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> selectedTimeFrom = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> selectedTimeTo = new MutableLiveData<>();
    private final MutableLiveData<Collection<EventCard>> events = new MutableLiveData<>();
    private int serviceId;

    private final MutableLiveData<Service> ServiceLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<Service> getServiceLiveData() {
        return ServiceLiveData;
    }

    public LiveData<Collection<Reservation>> getReservations() {
        return reservations;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getSelectedEventId() {
        return selectedEventId;
    }

    public LiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    public LiveData<LocalTime> getSelectedTimeFrom() {
        return selectedTimeFrom;
    }

    public LiveData<LocalTime> getSelectedTimeTo() {
        return selectedTimeTo;
    }

    public LiveData<Collection<EventCard>> getEvents() {
        return events;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void fetchReservations(int serviceId) {
        Call<Collection<Reservation>> call = ClientUtils.reservationService.getReservationsByServiceId(serviceId);
        call.enqueue(new Callback<Collection<Reservation>>() {
            @Override
            public void onResponse(Call<Collection<Reservation>> call, Response<Collection<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reservations.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch reservations.");
                }
            }

            @Override
            public void onFailure(Call<Collection<Reservation>> call, Throwable t) {
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void fetchEvents() {
        Call<Collection<EventCard>> call = ClientUtils.eventService.getAllByCreator();
        call.enqueue(new Callback<Collection<EventCard>>() {
            @Override
            public void onResponse(Call<Collection<EventCard>> call, Response<Collection<EventCard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch events.");
                }
            }

            @Override
            public void onFailure(Call<Collection<EventCard>> call, Throwable t) {
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public EventCard getEventByName(String eventName) {
        Collection<EventCard> eventList = events.getValue();
        if (eventList != null) {
            for (EventCard event : eventList) {
                if (event.getName().equals(eventName)) {
                    return event;
                }
            }
        }
        return null;
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.setValue(date);
    }

    public void setSelectedTimeFrom(LocalTime timeFrom) {
        selectedTimeFrom.setValue(timeFrom);
    }

    public void setSelectedTimeTo(LocalTime timeTo) {
        selectedTimeTo.setValue(timeTo);
    }

    public void fetchServiceById(int serviceId) {
        loading.setValue(true);
        Call<Service> call = ClientUtils.serviceService.getById(serviceId);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ServiceLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching service details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }

    public LocalTime calculateToTime(LocalTime fromTime) {
        Service Service = ServiceLiveData.getValue();
        if (Service != null && Service.getDuration() != 0) {
            return fromTime.plusMinutes(Service.getDuration());
        }
        return null;
    }

    public boolean isBookingValid(int eventId, LocalDate date, LocalTime timeFrom, LocalTime timeTo) {
        if (eventId <= 0) return false;
        if (date == null || date.isBefore(LocalDate.now())) return false;
        if (timeFrom == null || timeTo == null || timeFrom.isAfter(timeTo)) return false;
        return true;
    }

    public void bookService(int eventId, LocalDate date, LocalTime timeFrom, LocalTime timeTo, Consumer<Boolean> callback) {
        Reservation reservation = new Reservation(0, serviceId, eventId, date, timeFrom, timeTo, "PENDING");

        Call<Reservation> call = ClientUtils.reservationService.createReservation(reservation);
        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                callback.accept(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                callback.accept(false);
            }
        });
    }
}
