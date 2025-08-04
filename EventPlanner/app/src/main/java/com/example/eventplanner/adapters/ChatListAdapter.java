package com.example.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.ChatDetailFragment;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.Message;
import com.example.eventplanner.models.User;
import com.example.eventplanner.viewmodels.CommunicationViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListAdapter extends ArrayAdapter<Chat> {
    private CommunicationViewModel communicationViewModel;
    private ChatDetailFragment chatDetailFragment;
    private UserViewModel userViewModel;
    private FragmentActivity activity;
    private RelativeLayout chatCard;
    private ArrayList<Chat> chats;
    private ImageView chatImage;
    private User loggedUser;
    private ArrayList<User> allUsers;
    private TextView chatName, chatLastMessage, chatDate;
    private final Map<Integer, Message> lastMessageCache = new HashMap<>();
    private final Map<Integer, Void> lastMessageMap = new HashMap<>();

    public ChatListAdapter(FragmentActivity activity, CommunicationViewModel communicationViewModel) {
        super(activity, R.layout.chat_card);
        this.activity = activity;
        this.chats = new ArrayList<>();
        this.communicationViewModel = communicationViewModel;
        userViewModel = communicationViewModel.getUserViewModel();
        userViewModel.fetchAllUsers();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_card, parent, false);
        }

        Chat chat = getItem(position);

        initializeViews(convertView);
        setupViewModel();
        populateFields(chat);
        setupListeners(chat);

        return convertView;
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Nullable
    @Override
    public Chat getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        chatCard = convertView.findViewById(R.id.chatCard);
        chatImage = convertView.findViewById(R.id.chatImage);
        chatName = convertView.findViewById(R.id.chatName);
        chatLastMessage = convertView.findViewById(R.id.chatLastMessage);
        chatDate = convertView.findViewById(R.id.chatDate);
    }

    private void setupViewModel() {
        loggedUser = communicationViewModel.getLoggedUser();
        userViewModel.getAllUsers().observe(activity, users -> {
            if (users != null) {
                allUsers = users;
                notifyDataSetChanged();
            }
        });

        userViewModel.getErrorMessage().observe(activity, error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(Chat chat) {
        int otherUserId = loggedUser.getId() != chat.getUser1() ? chat.getUser1() : chat.getUser2();

        chatName.setText(getUserFullName(otherUserId));

        String imageUrl = getUserImage(otherUserId);
        Glide.with(activity)
                .load(imageUrl)
                .into(chatImage);

        if (lastMessageCache.containsKey(chat.getId())) {
            Message message = lastMessageCache.get(chat.getId());
            chatLastMessage.setText(message.getContent().isEmpty() ? "Start chatting!" : message.getContent());
            chatDate.setText(message.getDate() == null ? "" : formatMessageDate(message.getDate()));
        } else if (!lastMessageMap.containsKey(chat.getId())) {
            LiveData<Message> liveData = communicationViewModel.fetchLastMessage(chat.getId());

            liveData.observe(activity, message -> {
                if (message != null) {
                    lastMessageCache.put(chat.getId(), message);
                    notifyDataSetChanged();
                }
            });

            lastMessageMap.put(chat.getId(), null);
        } else {
            chatLastMessage.setText("Start chatting!");
            chatDate.setText("");
        }
    }

    private String formatMessageDate(LocalDateTime messageDateTime) {
        if (messageDateTime == null) return "";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(messageDateTime, now);

        if (duration.toHours() < 24 && messageDateTime.toLocalDate().equals(now.toLocalDate())) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return messageDateTime.format(timeFormatter);
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return messageDateTime.format(dateFormatter);
        }
    }

    private String getUserFullName(int userId) {
        if (allUsers == null) return "Unknown User";

        for (User user : allUsers) {
            if (user.getId() == userId) {
                return user.getFirstName() + " " + user.getLastName();
            }
        }

        return "Unknown User";
    }

    private String getUserImage(int userId) {
        if (allUsers == null) return "Unknown User";

        for (User user : allUsers) {
            if (user.getId() == userId) {
                return user.getImage();
            }
        }

        return "Unknown User";
    }

    private void setupListeners(Chat chat) {
        chatCard.setOnClickListener(view -> {
            chatDetailFragment = ChatDetailFragment.newInstance(communicationViewModel, chat, loggedUser, allUsers, loggedUser.getId() != chat.getUser1() ? chat.getUser1() : chat.getUser2());

            Bundle args = new Bundle();
            args.putInt("chatId", chat.getId());
            chatDetailFragment.setArguments(args);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.listViewCommunicationItems, chatDetailFragment, "ChatDetailFragment")
                    .addToBackStack(null)
                    .commit();

            activity.findViewById(R.id.communicationTitle).setVisibility(View.GONE);
            activity.findViewById(R.id.btnBack).setVisibility(View.GONE);
        });
    }

    public void updateChatsList(List<Chat> allChats) {
        if (allChats != null) {
            this.chats.clear();
            this.chats.addAll(allChats);
            notifyDataSetChanged();
        }
    }
}
