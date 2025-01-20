package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.Message;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunicationViewModel extends ViewModel {
    private Context context;
    private final MutableLiveData<ArrayList<Chat>> chatsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Message>> messagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<ArrayList<Chat>> getChats() {
        return chatsLiveData;
    }

    public LiveData<ArrayList<Message>> getMessages() {
        return messagesLiveData;
    }

    public void fetchChats(int userId) {
        Call<ArrayList<Chat>> call = ClientUtils.getCommunicationService(this.context).getChats(userId);
        call.enqueue(new Callback<ArrayList<Chat>>() {
            @Override
            public void onResponse(Call<ArrayList<Chat>> call, Response<ArrayList<Chat>> response) {
                if (response.isSuccessful()) {
                    chatsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch chats. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Chat>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
