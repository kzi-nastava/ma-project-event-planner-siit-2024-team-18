package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.EventListFragment;
import com.example.eventplanner.viewmodels.EventListViewModel;

public class AllEventsActivity extends BaseActivity {
    private ImageView addEventButton, btnBack;
    private EventListFragment eventListFragment;
    private EventListViewModel eventListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_events, findViewById(R.id.content_frame));

        initializeViews();
        initializeEventsFragment();
        setupUIInteractions();
        eventListViewModel.fetchEvents();
    }

    private void initializeViews() {
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        eventListViewModel.setContext(this);
        addEventButton = findViewById(R.id.addEvent);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupUIInteractions() {
        setupAddEventButton();
        setupBackButton();
    }

    private void initializeEventsFragment() {
        eventListFragment = EventListFragment.newInstance();
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        FragmentTransition.to(eventListFragment, this, false, R.id.listViewEvents);
    }

    private void setupAddEventButton() {
        addEventButton.setOnClickListener(v -> {
            Intent createIntent = new Intent(AllEventsActivity.this, CreateEventActivity.class);
            startActivity(createIntent);
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }
}
