package com.example.eventplanner.viewmodels;

import android.content.Context;
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
import com.example.eventplanner.models.NotificationModel;
import com.example.eventplanner.models.User;
import com.example.eventplanner.websocket.WebSocketManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunicationViewModel extends ViewModel {
    private static CommunicationViewModel instance;
    private Context context;
    private User loggedUser;
    private UserViewModel userViewModel;
    private WebSocketManager webSocketManager;
    private final MutableLiveData<ArrayList<Chat>> chatsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Message>> messagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Message> newMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<NotificationModel> newNotification = new MutableLiveData<>();
    private final MutableLiveData<List<NotificationModel>> notificationsLiveData = new MutableLiveData<>();
    private final Map<Integer, MutableLiveData<Message>> lastMessageMap = new HashMap<>();

    public LiveData<List<NotificationModel>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    private CommunicationViewModel() {}

    public static synchronized CommunicationViewModel getInstance() {
        if (instance == null) {
            instance = new CommunicationViewModel();
        }
        return instance;
    }

    public void initialize(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext();
        }
        getLoggedUser(context);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
    public LiveData<Message> getNewMessageLiveData() {
        return newMessageLiveData;
    }
    public void postNewMessage(Message message) {
        newMessageLiveData.postValue(message);
    }

    private void initializeWebSocketManager(User loggedUser) {
        webSocketManager = WebSocketManager.getInstance(context, loggedUser, this);
        fetchChats();
        fetchNotifications();
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
                    for (Chat chat : response.body()) {
                        webSocketManager.loadInitialChatSubscriptions(chat.getId());
                    }
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
        Call<ResponseBody> call = ClientUtils.getCommunicationService(this.context).getMessagesRaw(chatId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONArray array = new JSONArray(json);
                        ArrayList<Message> parsedMessages = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Message message = new Message();
                            message.setId(obj.getInt("id"));
                            message.setSenderId(obj.getInt("senderId"));
                            message.setChatId(obj.getInt("chatId"));
                            message.setContent(obj.getString("content"));

                            String dateStr = obj.getString("date");
                            LocalDateTime parsedDate = tryParseDate(dateStr);
                            message.setDate(parsedDate);

                            parsedMessages.add(message);
                        }

                        messagesLiveData.postValue(parsedMessages);

                    } catch (Exception e) {
                        errorMessage.postValue("Failed to parse messages: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    errorMessage.postValue("Failed to fetch messages. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    private LocalDateTime tryParseDate(String input) {
        List<DateTimeFormatter> formatters = List.of(
                new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .optionalStart()
                        .appendFraction(ChronoField.MICRO_OF_SECOND, 1, 6, true)
                        .optionalEnd()
                        .toFormatter(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (Exception ignored) {}
        }

        throw new RuntimeException("Unrecognized date format: " + input);
    }

    public LiveData<Message> fetchLastMessage(int chatId) {
        MutableLiveData<Message> liveData = new MutableLiveData<>();
        lastMessageMap.put(chatId, liveData);

        Call<ResponseBody> call = ClientUtils.getCommunicationService(this.context).getLastMessageRaw(chatId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        Message message = new Message();

                        message.setId(obj.getInt("id"));
                        message.setSenderId(obj.getInt("senderId"));
                        message.setChatId(obj.getInt("chatId"));
                        message.setContent(obj.getString("content"));

                        String dateStr = obj.getString("date");
                        message.setDate(tryParseDate(dateStr));

                        liveData.postValue(message);

                    } catch (Exception e) {
                        errorMessage.postValue("Failed to parse last message: " + e.getMessage());
                        e.printStackTrace();
                        liveData.postValue(null);
                    }
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    private void getLoggedUser(Context context) {
        userViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
        userViewModel.setContext(context);

        userViewModel.getLoggedUser().observe((LifecycleOwner) context, loggedUser -> {
            if (loggedUser != null) {
                this.loggedUser = loggedUser;

                initializeWebSocketManager(loggedUser);
            }
        });

        userViewModel.getErrorMessage().observe((LifecycleOwner) context, error -> {
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.fetchLoggedUser();
    }

    public LiveData<NotificationModel> getNewNotification() {
        return newNotification;
    }

    public void postNewNotification(NotificationModel notification) {
        newNotification.postValue(notification);

        List<NotificationModel> currentList = notificationsLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        } else {
            currentList = new ArrayList<>(currentList);
        }

        currentList.add(0, notification);
        notificationsLiveData.postValue(currentList);
    }


    public void fetchNotifications() {
        Call<List<NotificationModel>> call = ClientUtils.getNotificationService(this.context).getAllNotifications();
        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationsLiveData.postValue(response.body());
                    webSocketManager.subscribeToNotifications();
                } else {
                    errorMessage.postValue("Failed to fetch notifications. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                errorMessage.postValue("Failed to fetch notifications: " + t.getMessage());
            }
        });
    }

    public void toggleNotifications() {
        Call<ResponseBody> call = ClientUtils.getNotificationService(this.context).toggleNotifications();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Notification settings updated", Toast.LENGTH_SHORT).show();
                } else {
                    errorMessage.postValue("Failed to toggle notifications. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorMessage.postValue("Failed to toggle notifications: " + t.getMessage());
            }
        });
    }

    public void markNotificationAsSeen(int notificationId) {
        Call<NotificationModel> call = ClientUtils.getNotificationService(context).getNotificationById(notificationId);
        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to mark notification as seen. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
                errorMessage.postValue("Error: " + t.getMessage());
            }
        });
    }
}
