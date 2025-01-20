package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.ChatListFragment;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

public class CommunicationActivity extends BaseActivity {
    private ImageView btnBack;
    private CommunicationViewModel communicationViewModel;
    private ChatListFragment chatListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_communication, findViewById(R.id.content_frame));

        initializeViews();
        initializeChatFragment();
        setupListeners();

        loadChats();
    }

    private void initializeViews() {
        communicationViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);
        communicationViewModel.setContext(this);

        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeChatFragment() {
        chatListFragment = chatListFragment.newInstance(communicationViewModel);
        FragmentTransition.to(chatListFragment, this, false, R.id.listViewCommunicationItems);
    }

    private void setupListeners() {
        setupBackButton();
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void loadChats() {
        communicationViewModel.getChats();
    }
}
