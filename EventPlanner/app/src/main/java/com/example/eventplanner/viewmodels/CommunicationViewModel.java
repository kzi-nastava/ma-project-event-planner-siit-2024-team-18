package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.Message;
import com.example.eventplanner.models.User;
import com.example.eventplanner.websocket.WebSocketManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CommunicationViewModel extends ViewModel {
    private Context context;
    private User loggedUser;
    private UserViewModel userViewModel;
    private static final String TAG = "CommunicationViewModel";
    private final MutableLiveData<ArrayList<Chat>> chatsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Message>> messagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Message> lastMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    private WebSocketManager webSocketManager;
    private final MutableLiveData<List<String>> realTimeMessages = new MutableLiveData<>(new ArrayList<>());

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
        getLoggedUser(context);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
    public UserViewModel getUserViewModel() {
        return userViewModel;
    }
    public LiveData<ArrayList<Chat>> getChats() {
        return chatsLiveData;
    }

    public LiveData<ArrayList<Message>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<Message> getLastMessage() {
        return lastMessageLiveData;
    }

    public LiveData<List<String>> getRealTimeMessages() {
        return realTimeMessages;
    }

    private void initializeWebSocketManager() {
        if (context != null) {
            webSocketManager = WebSocketManager.getInstance(context, null);
            webSocketManager.connect();
            webSocketManager.subscribeToChat();
        }
    }

    public void sendMessage(String content, int chatId, int senderId) {
        webSocketManager.sendMessage(content, chatId, senderId);
    }

    public void unsubscribeFromMessages() {
        webSocketManager.disconnect();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unsubscribeFromMessages();
    }

    public void fetchChats() {
        Call<ArrayList<Chat>> call = ClientUtils.getCommunicationService(this.context).getChats(loggedUser.getId());
        call.enqueue(new Callback<ArrayList<Chat>>() {
            @Override
            public void onResponse(Call<ArrayList<Chat>> call, retrofit2.Response<ArrayList<Chat>> response) {
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

    public void fetchMessages(int chatId) {
        Call<ArrayList<Message>> call = ClientUtils.getCommunicationService(this.context).getMessages(chatId);
        call.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, retrofit2.Response<ArrayList<Message>> response) {
                if (response.isSuccessful()) {
                    messagesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch messages. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void fetchLastMessage(int chatId) {
        Call<Message> call = ClientUtils.getCommunicationService(this.context).getLastMessage(chatId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, retrofit2.Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lastMessageLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("No last message found. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void subscribeToMessages() {
        Log.d(TAG, "Subscribed to WebSocket messages");
    }

    private void getLoggedUser(Context context) {
        userViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
        userViewModel.setContext(context);

        userViewModel.getLoggedUser().observe((LifecycleOwner) context, loggedUser -> {
            if (loggedUser != null) {
                this.loggedUser = loggedUser;
                fetchChats();
                initializeWebSocketManager();
            }
        });

        userViewModel.getErrorMessage().observe((LifecycleOwner) context, error -> {
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.fetchLoggedUser();
    }
}
