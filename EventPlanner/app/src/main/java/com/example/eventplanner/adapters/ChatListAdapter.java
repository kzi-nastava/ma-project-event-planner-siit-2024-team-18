package com.example.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.ChatDetailFragment;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.Message;
import com.example.eventplanner.models.User;
import com.example.eventplanner.viewmodels.CommunicationViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatListAdapter extends ArrayAdapter<Chat> {
    private CommunicationViewModel communicationViewModel;
    private ChatDetailFragment chatDetailFragment;
    private UserViewModel userViewModel;
    private FragmentActivity activity;
    private LinearLayout chatCard;
    private ArrayList<Chat> chats;
    private ImageView chatImage;
    private User loggedUser;
    private ArrayList<User> allUsers;
    private TextView chatName, chatLastMessage, chatDate;
    private Map<ArrayList<Integer>, Message> lastMessages;

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
        chatName.setText(getUserFullName(loggedUser.getId() != chat.getUser1() ? chat.getUser1() : chat.getUser2()));

        String imageUrl = getUserImage(loggedUser.getId() != chat.getUser1() ? chat.getUser1() : chat.getUser2());
        Glide.with(activity)
                .load(imageUrl)
                .into(chatImage);

//        lastMessages[chat.id]?.content === 'Start chatting!' ? '' : (lastMessages[chat.id]?.date ? formatDateOrTime(lastMessages[chat.id]!.date) : '')

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
            // Remove the existing ChatDetailFragment if it's already open
            Fragment existingFragment = activity.getSupportFragmentManager().findFragmentByTag("ChatListFragment");
            if (existingFragment != null) {
                activity.getSupportFragmentManager().beginTransaction().remove(existingFragment).commit();
            }

            // Initialize the new ChatDetailFragment
            chatDetailFragment = ChatDetailFragment.newInstance(communicationViewModel, chat, loggedUser, allUsers);
            Bundle args = new Bundle();
            args.putInt("chatId", chat.getId());
            chatDetailFragment.setArguments(args);

            // Transition to the new fragment
            FragmentTransition.to(chatDetailFragment, activity, true, R.id.content_frame);
            activity.findViewById(R.id.btnBack).setVisibility(View.GONE);
            activity.findViewById(R.id.communicationTitle).setVisibility(View.GONE);
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
