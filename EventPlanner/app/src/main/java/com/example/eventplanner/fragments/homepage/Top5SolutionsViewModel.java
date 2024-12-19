package com.example.eventplanner.fragments.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.SolutionCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Top5SolutionsViewModel extends ViewModel {

    private final MutableLiveData<List<SolutionCard>> top5SolutionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<SolutionCard>> getTop5Solutions() {
        return top5SolutionsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchTop5Solutions() {
        if (Boolean.TRUE.equals(loading.getValue())) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<List<SolutionCard>> call = ClientUtils.solutionService.getTopSolutions();
        call.enqueue(new Callback<List<SolutionCard>>() {
            @Override
            public void onResponse(Call<List<SolutionCard>> call, Response<List<SolutionCard>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    top5SolutionsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching top 5 solutions: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<SolutionCard>> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
