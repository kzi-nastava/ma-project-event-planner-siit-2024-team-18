package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.PagedResponse;
import com.example.eventplanner.models.SolutionCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductsServicesViewModel extends ViewModel {

    private final MutableLiveData<List<SolutionCard>> solutionCardsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPageLiveData = new MutableLiveData<>(0);

    private int currentPage = 0;
    private int itemsPerPage = 10;
    private int totalPages = 1;

    private String searchQuery = "";
    private String city = null;
    private Boolean isProductOnly = true;
    private String name = null;
    private String description = null;
    private Double price = null;
    private Double discount = null;
    private String category = null;
    private String providerFirstName = null;
    private String country = null;
    private String startDate = null;
    private String endDate = null;
    private String selectedFilter = "name";
    private String sortOrder = "ASC";

    private final Map<String, String> filterMapping = new HashMap<>();

    public LiveData<List<SolutionCard>> getSolutionCards() {
        return solutionCardsLiveData;
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

    public void fetchSolutionCards() {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<PagedResponse<SolutionCard>> call = ClientUtils.getSolutionService(this.context).searchAndFilterSolutions(
                searchQuery,
                city,
                isProductOnly,
                name,
                description,
                price,
                discount,
                category,
                providerFirstName,
                country,
                formatDate(startDate),
                formatDate(endDate),
                currentPage,
                itemsPerPage,
                determineSortBy(selectedFilter),
                sortOrder
        );

        call.enqueue(new Callback<PagedResponse<SolutionCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<SolutionCard>> call, Response<PagedResponse<SolutionCard>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<SolutionCard> pagedResponse = response.body();
                    solutionCardsLiveData.setValue(pagedResponse.getContent());
                    totalPages = pagedResponse.getTotalPages();
                } else {
                    errorMessage.setValue("Error fetching solution cards: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<SolutionCard>> call, Throwable t) {
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

    public void setCity(String city) {
        this.city = city;
        resetPagination();
    }

    public void setIsProductOnly(Boolean isProductOnly) {
        this.isProductOnly = isProductOnly;
        resetPagination();
    }

    public void setName(String name) {
        this.name = name;
        resetPagination();
    }

    public void setDescription(String description) {
        this.description = description;
        resetPagination();
    }

    public void setPrice(Double price) {
        this.price = price;
        resetPagination();
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
        resetPagination();
    }

    public void setCategory(String category) {
        this.category = category;
        resetPagination();
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
        resetPagination();
    }

    public void setCountry(String country) {
        this.country = country;
        resetPagination();
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
        resetPagination();
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
            fetchSolutionCards();
        }
    }

    public void goToPreviousPage() {
        if (hasPreviousPage()) {
            currentPage--;
            currentPageLiveData.setValue(currentPage);
            fetchSolutionCards();
        }
    }

    private void resetPagination() {
        currentPage = 0;
        currentPageLiveData.setValue(0);
        fetchSolutionCards();
    }

    private String determineSortBy(String filter) {
        if (List.of("country", "city", "providerFirstName").contains(filter)) {
            return "name";
        }
        return filter;
    }

    private String formatDate(String date) {
        return date != null ? date : "";
    }
}
