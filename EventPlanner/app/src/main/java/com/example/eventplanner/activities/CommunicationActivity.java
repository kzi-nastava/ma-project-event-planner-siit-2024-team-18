package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

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
    }

    private void initializeViews() {
        communicationViewModel = new ViewModelProvider(this).get(CommunicationViewModel.class);
        communicationViewModel.setContext(this);

        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeChatFragment() {
        chatListFragment = chatListFragment.newInstance(communicationViewModel);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.listViewCommunicationItems, chatListFragment, "ChatListFragment")
                .commit();
    }

    private void setupListeners() {
        setupBackButton();
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }
}
