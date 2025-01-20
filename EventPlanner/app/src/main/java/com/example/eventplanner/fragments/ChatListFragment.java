package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ChatListAdapter;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

public class ChatListFragment extends ListFragment {
    private ChatListAdapter adapter;
    private CommunicationViewModel communicationViewModel;

    public static ChatListFragment newInstance(CommunicationViewModel communicationViewModel) {
        ChatListFragment fragment = new ChatListFragment();
        fragment.setCommunicationViewModel(communicationViewModel);
        return fragment;
    }

    public void setCommunicationViewModel(CommunicationViewModel communicationViewModel) {
        this.communicationViewModel = communicationViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ChatListAdapter(requireActivity(), communicationViewModel);
        setListAdapter(adapter);

        communicationViewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null) {
                adapter.updateChatsList(chats);
            }
        });

        communicationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        communicationViewModel.fetchChats(2);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
