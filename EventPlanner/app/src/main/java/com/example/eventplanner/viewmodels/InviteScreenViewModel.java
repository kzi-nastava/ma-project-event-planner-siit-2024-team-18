package com.example.eventplanner.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.EventInvitation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteScreenViewModel extends ViewModel {

    private final MutableLiveData<List<String>> emailList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isSending = new MutableLiveData<>(false);
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public LiveData<List<String>> getEmailList() {
        return emailList;
    }

    public LiveData<Boolean> getIsSending() {
        return isSending;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void addEmail(String email) {
        if (email.isEmpty() || emailList.getValue().contains(email)) {
            toastMessage.setValue("Enter a valid and unique email");
            return;
        }

        List<String> currentList = new ArrayList<>(emailList.getValue());
        currentList.add(email);
        emailList.setValue(currentList);
    }

    public void removeEmail(String email) {
        List<String> currentList = new ArrayList<>(emailList.getValue());
        currentList.remove(email);
        emailList.setValue(currentList);
    }

    public void sendInvites(int eventId) {
        if (emailList.getValue().isEmpty()) {
            toastMessage.setValue("No emails to send!");
            return;
        }

        isSending.setValue(true);

        ClientUtils.emailService.sendEventInvitations(eventId, emailList.getValue())
                .enqueue(new Callback<EventInvitation>() {
                    @Override
                    public void onResponse(Call<EventInvitation> call, Response<EventInvitation> response) {
                        if (response.isSuccessful()) {
                            toastMessage.postValue("Invites sent successfully!");
                            emailList.postValue(new ArrayList<>());
                        } else {
                            toastMessage.postValue("Failed to send invites. Please try again later.");
                        }
                        isSending.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<EventInvitation> call, Throwable t) {
                        toastMessage.postValue("Error: " + t.getMessage());
                        isSending.postValue(false);
                    }
                });
    }

    public void showToast(String message) {
        toastMessage.setValue(message);
    }
}
