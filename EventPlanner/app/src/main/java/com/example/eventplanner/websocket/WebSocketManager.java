package com.example.eventplanner.websocket;

import android.content.Context;
import android.util.Log;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.models.User;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private StompClient stompClient;
    private final Context context;
    private final Set<String> activeChatSubscriptions = new HashSet<>();
    private CompositeDisposable compositeDisposable;
    private String websocketUrl = "ws://" + BuildConfig.IP_ADDR + ":8080/chat/websocket";
    private User loggedUser;

    private WebSocketManager(Context context, User loggedUser) {
        this.context = context;
        this.loggedUser = loggedUser;
        this.stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketUrl);
        resetSubscriptions();
    }

    public static synchronized WebSocketManager getInstance(Context context, User loggedUser) {
        if (instance == null) {
            instance = new WebSocketManager(context, loggedUser);
        }
        return instance;
    }

    public void connect() {
        stompClient.withClientHeartbeat(5000).withServerHeartbeat(5000);
        resetSubscriptions();

        // Handle lifecycle events
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

        compositeDisposable.add(dispLifecycle);

        Disposable dispTopic = stompClient.topic("/topic/messages/1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
//                    addItem(mGson.fromJson(topicMessage.getPayload(), Message.class));
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);

        // Add connection headers
        String authToken = retrieveAuthToken();

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + authToken));

        stompClient.connect(headers);
    }

    private String retrieveAuthToken() {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("user_token", null);
    }

    private void loadInitialChatSubscriptions() {
        // Simulate fetching chats from backend (replace with real API call)
        // Assume `getUserChats` returns a list of chat IDs for the logged user
        Set<Integer> userChats = getUserChats();
        for (int chatId : userChats) {
            subscribeToChat();
        }
    }

    private Set<Integer> getUserChats() {
        // Mocked chat list for demonstration
        Set<Integer> chats = new HashSet<>();
        chats.add(1); // Replace with actual API call
        chats.add(2);
        return chats;
    }

    public void subscribeToChat() {
        String topic = "/topic/messages/" + 1;
        if (activeChatSubscriptions.contains(topic)) {
            Log.d(TAG, "Already subscribed to topic: " + topic);
            return;
        }

        Disposable topicDisp = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    Log.d(TAG, "Received message: " + message.getPayload());
                    handleIncomingMessage(message.getPayload(), 1);
                }, throwable -> Log.e(TAG, "Error on topic subscription", throwable));

        compositeDisposable.add(topicDisp);
        activeChatSubscriptions.add(topic);
    }

    private void handleIncomingMessage(String payload, int chatId) {
        try {
            JSONObject messageJson = new JSONObject(payload);
            // Parse and process the message
            int senderId = messageJson.getInt("senderId");
            String content = messageJson.getString("content");

            Log.d(TAG, "New message in chat " + chatId + " from sender " + senderId + ": " + content);

            // Example: Notify the UI about the new message
            // You can update a LiveData object or use an event bus
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing message JSON", e);
        }
    }

    public void sendMessage(String content, int chatId, int senderId) {
        try {
            JSONObject message = new JSONObject();
            message.put("content", content);
            message.put("chatId", chatId);
            message.put("senderId", senderId);

            stompClient.send("/app/chat", message.toString())
                    .subscribe(() -> Log.d(TAG, "Message sent: " + message),
                            throwable -> Log.e(TAG, "Error sending message", throwable));
        } catch (JSONException e) {
            Log.e(TAG, "Error creating message JSON", e);
        }
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void disconnect() {
        compositeDisposable.dispose();
        stompClient.disconnect();
        activeChatSubscriptions.clear();
        Log.d(TAG, "Disconnected from WebSocket");
    }
}
