package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.MessageListAdapter;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

import java.util.ArrayList;

public class ChatDetailFragment extends Fragment {
    private CommunicationViewModel communicationViewModel;
    private MessageListAdapter adapter;
    private ListView messageListView;
    private TextView chatTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        communicationViewModel = new ViewModelProvider(requireActivity()).get(CommunicationViewModel.class);
        messageListView = view.findViewById(R.id.messageListView);
        chatTitle = view.findViewById(R.id.chatTitle);

        adapter = new MessageListAdapter(requireContext(), new ArrayList<>());
        messageListView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        communicationViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateMessageList(messages);
        });

        communicationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

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
