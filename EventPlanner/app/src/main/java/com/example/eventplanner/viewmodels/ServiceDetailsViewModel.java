package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Grade;
import com.example.eventplanner.models.Service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsViewModel extends ViewModel {
    private final MutableLiveData<Service> serviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<Grade> serviceGradeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> serviceReviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> successChat = new MutableLiveData<>();
    public LiveData<Boolean> getSuccess() {
        return success;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getSuccessChat() {
        return successChat;
    }

    public void setService(Service service) {
        serviceLiveData.setValue(service);
    }

    public LiveData<Service> getService() {return serviceLiveData;}
    public LiveData<Grade> getServiceGrade() {
        return serviceGradeLiveData;
    }

    public LiveData<Integer> getServiceReviews() {
        return serviceReviewsLiveData;
    }

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
                Log.e("ServiceDetailsActivity", "Failed to fetch service by id", t);
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

    public void fetchServiceRating(int serviceId) {
        fetchServiceGrade(serviceId);
        fetchServiceReviews(serviceId);
    }

    private void fetchServiceGrade(int serviceId) {
        errorMessage.setValue(null);

        Call<Grade> call = ClientUtils.getServiceService(this.context).getServiceGrade(serviceId);
        call.enqueue(new Callback<Grade>() {
            @Override
            public void onResponse(Call<Grade> call, Response<Grade> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceGradeLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching service grade: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Grade> call, Throwable t) {
                Log.e("ServiceDetailsActivity", "Failed to fetch service grade", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private void fetchServiceReviews(int serviceId) {
        errorMessage.setValue(null);

        Call<Integer> call = ClientUtils.getServiceService(this.context).getServiceReviews(serviceId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceReviewsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching service reviews: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("ServiceDetailsActivity", "Failed to fetch service reviews", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void rateService(int serviceId, int value, String comment) {
        errorMessage.setValue(null);

        Grade grade = new Grade(value, comment);
        Call<ResponseBody> call = ClientUtils.getServiceService(this.context).rateService(serviceId, grade);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    fetchServiceRating(serviceId);
                    Log.d("Rating", "Service rated successfully!");
                } else {
                    errorMessage.setValue("Error rating service: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchChats(int solutionId) {
        Call<ResponseBody> call = ClientUtils.getServiceService(this.context).getChat(solutionId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    successChat.postValue(true);
                } else {
                    errorMessage.postValue("Failed to fetch chats. Code: " + response.code());
                    successChat.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
                successChat.postValue(false);
            }
        });
    }
}
