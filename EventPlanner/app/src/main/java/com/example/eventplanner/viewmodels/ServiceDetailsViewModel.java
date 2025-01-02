package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsViewModel extends ViewModel {
    private final MutableLiveData<Service> serviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    public LiveData<Boolean> getSuccess() {
        return success;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setService(Service service) {
        serviceLiveData.setValue(service);
    }

    public LiveData<Service> getService() {return serviceLiveData;}

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchServiceById(int ServiceId) {
        Call<Service> call = ClientUtils.getServiceService(this.context).getById(ServiceId);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    serviceLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch Service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addNewService(RequestBody nameBody, RequestBody descriptionBody, RequestBody specificsBody, RequestBody categoryBody, RequestBody eventTypesBody, RequestBody locationBody, RequestBody reservationDeadlineBody, RequestBody cancellationDeadlineBody, RequestBody priceBody, RequestBody discountBody, RequestBody durationBody, RequestBody minEngagementBody, RequestBody maxEngagementBody, RequestBody visibleBody, RequestBody availableBody, RequestBody reservationTypeBody, RequestBody workingHoursStartBody, RequestBody workingHoursEndBody, List<MultipartBody.Part> imageParts) {
        Call<Service> call = ClientUtils.getServiceService(this.context).add(nameBody, descriptionBody, specificsBody, categoryBody, eventTypesBody, locationBody, reservationDeadlineBody, cancellationDeadlineBody, priceBody, discountBody, durationBody, minEngagementBody, maxEngagementBody, visibleBody, availableBody, reservationTypeBody, workingHoursStartBody, workingHoursEndBody, imageParts);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to add service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editService(int id, RequestBody nameBody, RequestBody descriptionBody, RequestBody specificsBody, RequestBody categoryBody, RequestBody eventTypesBody, RequestBody locationBody, RequestBody reservationDeadlineBody, RequestBody cancellationDeadlineBody, RequestBody priceBody, RequestBody discountBody, RequestBody durationBody, RequestBody minEngagementBody, RequestBody maxEngagementBody, RequestBody visibleBody, RequestBody availableBody, RequestBody reservationTypeBody, RequestBody workingHoursStartBody, RequestBody workingHoursEndBody, List<MultipartBody.Part> imageParts) {
        Call<Service> call = ClientUtils.getServiceService(this.context).edit(id, nameBody, descriptionBody, specificsBody, categoryBody, eventTypesBody, locationBody, reservationDeadlineBody, cancellationDeadlineBody, priceBody, discountBody, durationBody, minEngagementBody, maxEngagementBody, visibleBody, availableBody, reservationTypeBody, workingHoursStartBody, workingHoursEndBody, imageParts);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
