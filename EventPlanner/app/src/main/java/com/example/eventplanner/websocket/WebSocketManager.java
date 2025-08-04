package com.example.eventplanner.websocket;

import android.content.Context;
import android.util.Log;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.models.Message;
import com.example.eventplanner.models.NotificationModel;
import com.example.eventplanner.models.User;
import com.example.eventplanner.utils.NotificationHelper;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketManager {
    private String websocketUrl = "ws://" + BuildConfig.IP_ADDR + ":8080/chat/websocket";
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private boolean notificationsMuted = false;
    private StompClient stompClient;
    private final Context context;
    private final Set<String> activeChatSubscriptions = new HashSet<>();
    private final Set<String> activeNotificationSubscriptions = new HashSet<>();
    private CompositeDisposable chatDisposable;
    private CompositeDisposable notificationDisposable;
    private User loggedUser;
    private CommunicationViewModel communicationViewModel;

    private WebSocketManager(Context context, User loggedUser, CommunicationViewModel communicationViewModel) {
        this.context = context;
        this.loggedUser = loggedUser;
        this.communicationViewModel = communicationViewModel;
        this.stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketUrl);
        resetSubscriptions();
    }

    public static synchronized WebSocketManager getInstance(Context context, User loggedUser, CommunicationViewModel communicationViewModel) {
        if (instance == null) {
            instance = new WebSocketManager(context, loggedUser, communicationViewModel);
            instance.connect();
        }
        return instance;
    }

    public void connect() {
        stompClient.withClientHeartbeat(5000).withServerHeartbeat(5000);
        resetSubscriptions();

        Disposable dispLifecycle = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "STOMP connection opened for userId: " + loggedUser.getId());
                            break;
                        case ERROR:
                            Log.e(TAG, "STOMP connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "STOMP connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "Failed server heartbeat");
                            break;
                    }
                });

        chatDisposable.add(dispLifecycle);

        String authToken = retrieveAuthToken();

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + authToken));

        stompClient.connect(headers);
    }

    public void subscribeToNotifications() {
        String topic = "/topic/notifications/" + loggedUser.getId();

        if (activeNotificationSubscriptions.contains(topic)) {
            Log.d(TAG, "Already subscribed to notification topic: " + topic);
            return;
        }

        Disposable topicDisp = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    Log.d(TAG, "Received notification: " + message.getPayload());
                    if (!notificationsMuted) {
                        handleIncomingNotification(message.getPayload());
                    } else {
                        Log.d(TAG, "Notification muted");
                    }
                }, throwable -> Log.e(TAG, "Error on notification topic", throwable));

        notificationDisposable.add(topicDisp);
        activeNotificationSubscriptions.add(topic);
        Log.d(TAG, "Subscribed to notification topic: " + topic);
    }



    private void handleIncomingNotification(String payload) {
        try {
            JSONObject json = new JSONObject(payload);

            int id = json.getInt("id");
            String title = json.getString("title");
            String content = json.getString("content");
            boolean seen = json.getBoolean("seen");
            String type = json.getString("notificationType");
            int itemId = json.getInt("itemId");
            String dateString = json.optString("date", null);

            NotificationModel notification = new NotificationModel(id, title, content, seen, type, itemId);

            if (dateString != null) {
                try {
                    LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    notification.setDate(date);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse date: " + dateString, e);
                }
            }

            communicationViewModel.postNewNotification(notification);

            NotificationHelper.showNotification(context, notification.getTitle(), notification.getContent());

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing notification JSON", e);
        }
    }

    public void setNotificationsMuted(boolean muted) {
        this.notificationsMuted = muted;
    }


    private String retrieveAuthToken() {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("user_token", null);
    }

    public void loadInitialChatSubscriptions(int chatId) {
        String topic = "/topic/messages/" + chatId;
        if (activeChatSubscriptions.contains(topic)) {
            Log.d(TAG, "Already subscribed to chat topic: " + topic);
            return;
        }

        Disposable topicDisp = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    Log.d(TAG, "Received message: " + message.getPayload());
                    handleIncomingMessage(message.getPayload(), chatId);
                }, throwable -> Log.e(TAG, "Error on chat topic", throwable));

        chatDisposable.add(topicDisp);
        activeChatSubscriptions.add(topic);
    }

    private void handleIncomingMessage(String payload, int chatId) {
        try {
            JSONObject messageJson = new JSONObject(payload);
            int messageId = messageJson.getInt("id");
            int senderId = messageJson.getInt("senderId");
            String content = messageJson.getString("content");
            String dateString = messageJson.getString("date");

            Log.d(TAG, "New message in chat " + chatId + " from sender " + senderId + ": " + content);

            LocalDateTime date;
            try {
                date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                Log.e(TAG, "Fallback to manual parse due to format error", e);
                date = LocalDateTime.now();
            }
            Message newMessage = new Message();
            newMessage.setId(messageId);
            newMessage.setSenderId(senderId);
            newMessage.setChatId(chatId);
            newMessage.setContent(content);
            newMessage.setDate(date);

            communicationViewModel.postNewMessage(newMessage);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing message JSON", e);
        }
    }

    public void sendMessage(String content, int chatId, int senderId) {
        try {
            JSONObject message = new JSONObject();
            message.put("content", content);
            message.put("chatId", chatId);
            message.put("date", LocalDateTime.now());
            message.put("senderId", senderId);

            stompClient.send("/app/send", message.toString())
                    .subscribe(() -> Log.d(TAG, "Message sent: " + message),
                            throwable -> Log.e(TAG, "Error sending message", throwable));
        } catch (JSONException e) {
            Log.e(TAG, "Error creating message JSON", e);
        }
    }

    private void resetSubscriptions() {
        if (chatDisposable != null) chatDisposable.dispose();
        if (notificationDisposable != null) notificationDisposable.dispose();

        chatDisposable = new CompositeDisposable();
        notificationDisposable = new CompositeDisposable();

        activeChatSubscriptions.clear();
        activeNotificationSubscriptions.clear();
    }

    public void disconnect() {
        if (chatDisposable != null) chatDisposable.dispose();
        if (notificationDisposable != null) notificationDisposable.dispose();
        if (stompClient != null) stompClient.disconnect();

        activeChatSubscriptions.clear();
        activeNotificationSubscriptions.clear();

        Log.d(TAG, "Disconnected from WebSocket");
    }
}
