package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.MessageListAdapter;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.User;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

import java.util.ArrayList;

public class ChatDetailFragment extends ListFragment {
    private CommunicationViewModel communicationViewModel;
    private MessageListAdapter adapter;
    private Chat chat;
    private User loggedUser;
    private TextView chatTitle;
    private ArrayList<User> allUsers;

    public static ChatDetailFragment newInstance(CommunicationViewModel communicationViewModel, Chat chat, User loggedUser, ArrayList<User> allUsers) {
        ChatDetailFragment fragment = new ChatDetailFragment();
        fragment.setParameters(communicationViewModel, chat, loggedUser, allUsers);
        return fragment;
    }

    public void setParameters(CommunicationViewModel communicationViewModel, Chat chat, User loggedUser, ArrayList<User> allUsers) {
        this.communicationViewModel = communicationViewModel;
        this.chat = chat;
        this.allUsers = allUsers;
        this.loggedUser = loggedUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.chatTitle)).setText(Integer.toString(chat.getId()));

        adapter = new MessageListAdapter(requireContext(), communicationViewModel, chat, loggedUser, allUsers);
        setListAdapter(adapter);

        communicationViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateMessageList(messages);
        });

        communicationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        communicationViewModel.fetchMessages(chat.getId());
        communicationViewModel.fetchChats();
        communicationViewModel.subscribeToMessages();

        // Example: Send message
//        view.findViewById(R.id.sendButton).setOnClickListener(v -> {
//            String messageContent = "Hello, WebSocket!"; // Replace with actual input
//            int chatId = 1; // Replace with actual chat ID
//            int senderId = 123; // Replace with actual sender ID
//            communicationViewModel.sendMessage(messageContent, chatId, senderId);
//        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        communicationViewModel.unsubscribeFromMessages();
    }
}
