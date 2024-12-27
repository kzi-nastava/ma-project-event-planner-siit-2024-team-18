package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.PagedResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllEventsViewModel extends ViewModel {

    private final MutableLiveData<List<EventCard>> eventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPageLiveData = new MutableLiveData<>(0);

    private int currentPage = 0;
    private int eventsPerPage = 10;
    private int totalPages = 1;

    private String searchQuery = "";
    private String fromDate = null;
    private String toDate = null;
    private String selectedFilter = "name";
    private String sortOrder = "ASC";

    private final Map<String, String> filterMapping = new HashMap<>();

    public LiveData<List<EventCard>> getEvents() {
        return eventsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getPageLiveData() {
        return currentPageLiveData;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setFilterMapping(Map<String, String> mapping) {
        filterMapping.clear();
        filterMapping.putAll(mapping);
    }

    public void fetchEvents() {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<PagedResponse<EventCard>> call = ClientUtils.getEventService(this.context).searchAndFilterEvents(
                searchQuery,
                null,
                fromDate != null ? LocalDate.parse(fromDate) : null,
                toDate != null ? LocalDate.parse(toDate) : null,
                null,
                selectedFilter.equals("maxParticipants") ? parseInteger(searchQuery) : null,
                selectedFilter.equals("budget") ? parseDouble(searchQuery) : null,
                selectedFilter.equals("eventType") ? searchQuery : null,
                null,
                currentPage,
                eventsPerPage,
                determineSortBy(selectedFilter),
                sortOrder
        );

        call.enqueue(new Callback<PagedResponse<EventCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<EventCard>> call, Response<PagedResponse<EventCard>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<EventCard> pagedResponse = response.body();
                    eventsLiveData.setValue(pagedResponse.getContent());
                    totalPages = pagedResponse.getTotalPages();
                } else {
                    errorMessage.setValue("Error fetching events: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<EventCard>> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error");
            }
        });
    }

    public void setSelectedFilter(String selectedLabel) {
        this.selectedFilter = filterMapping.getOrDefault(selectedLabel, "name");
        resetPagination();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        resetPagination();
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
        resetPagination();
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
        resetPagination();
    }

    public void setSortOrder(String order) {
        this.sortOrder = order;
        resetPagination();
    }

    public boolean hasNextPage() {
        return currentPage + 1 < totalPages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public void goToNextPage() {
        if (hasNextPage()) {
            currentPage++;
            currentPageLiveData.setValue(currentPage);
            fetchEvents();
        }
    }

    public void goToPreviousPage() {
        if (hasPreviousPage()) {
            currentPage--;
            currentPageLiveData.setValue(currentPage);
            fetchEvents();
        }
    }

    private void resetPagination() {
        currentPage = 0;
        currentPageLiveData.setValue(0);
        fetchEvents();
    }

    private String determineSortBy(String filter) {
        if (List.of("country", "city", "organizerFirstName").contains(filter)) {
            return null;
        }
        return filter;
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
