package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private ChatListFragment chatListFragment;
    private MessageListAdapter adapter;
    private Chat chat;
    private User loggedUser;
    private User recipient;
    private TextView chatTitle;
    private ArrayList<User> allUsers;

    public static ChatDetailFragment newInstance(CommunicationViewModel communicationViewModel, Chat chat, User loggedUser, ArrayList<User> allUsers, int recipientId) {
        ChatDetailFragment fragment = new ChatDetailFragment();
        fragment.setParameters(communicationViewModel, chat, loggedUser, allUsers, recipientId);
        return fragment;
    }

    public void setParameters(CommunicationViewModel communicationViewModel, Chat chat, User loggedUser, ArrayList<User> allUsers, int recipientId) {
        this.communicationViewModel = communicationViewModel;
        this.chat = chat;
        this.allUsers = allUsers;
        this.loggedUser = loggedUser;
        for (User user : allUsers) {
            if (user.getId() == recipientId) {
                this.recipient = user;
                break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        }

        view.setPadding(0, 0, 0, 0);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();

        ((TextView) view.findViewById(R.id.chatTitle)).setText(recipient.getFirstName() + " " + recipient.getLastName());

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

        communicationViewModel.getNewMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !adapter.containsMessage(message)) {
                adapter.addNewMessage(message);
            }
            getListView().smoothScrollToPosition(adapter.getCount() - 1);

        });
        communicationViewModel.fetchMessages(chat.getId());

        communicationViewModel.fetchChats();

        view.findViewById(R.id.buttonSend).setOnClickListener(v -> {

            EditText editTextMessage = view.findViewById(R.id.editTextMessage);
            String newMessage = String.valueOf(editTextMessage.getText());

            communicationViewModel.sendMessage(newMessage, chat.getId(), loggedUser.getId());
            editTextMessage.setText("");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().findViewById(R.id.communicationTitle).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        requireActivity().findViewById(R.id.btnBackDM).setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
