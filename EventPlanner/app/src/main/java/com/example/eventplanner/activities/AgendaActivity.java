package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.AddActivityDialog;
import com.example.eventplanner.fragments.ActivityListFragment;
import com.example.eventplanner.models.EventActivity;
import com.example.eventplanner.viewmodels.AgendaViewModel;

import java.time.LocalDateTime;

public class AgendaActivity extends BaseActivity {
    private ImageView addActivity, btnBack;
    private AgendaViewModel agendaViewModel;
    private ActivityListFragment activityListFragment;
    private int eventId;
    private LocalDateTime eventStartDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_agenda, findViewById(R.id.content_frame));

        this.eventId = getIntent().getIntExtra("eventId", -1);
        this.eventStartDateTime = LocalDateTime.parse(getIntent().getStringExtra("eventStartDateTime"));

        initializeViews();
        initializeActivitiesFragment();
        setupListeners();
    }

    private void initializeViews() {
        addActivity = findViewById(R.id.addActivity);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeActivitiesFragment() {
        agendaViewModel = new ViewModelProvider(this).get(AgendaViewModel.class);
        agendaViewModel.setContext(this);
        activityListFragment = ActivityListFragment.newInstance(agendaViewModel, eventId);

        FragmentTransition.to(activityListFragment, this, false, R.id.listViewActivities);
    }

    private void setupListeners() {
        setupAddActivityButton();
        setupBackButton();
    }

    private void setupAddActivityButton() {
        addActivity.setOnClickListener(v -> {
            AddActivityDialog.show(eventStartDateTime, agendaViewModel.getLastActivity(), this, (activityName, description, location, startDate, endDate) -> {
                agendaViewModel.addActivity(eventId, new EventActivity(0, activityName, description, location, startDate, endDate));
            });
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.addActivity).setVisibility(View.GONE);
        findViewById(R.id.btnBack).setVisibility(View.GONE);
        findViewById(R.id.listViewActivitiesContainer).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.addActivity).setVisibility(View.VISIBLE);
        findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
        findViewById(R.id.listViewActivitiesContainer).setVisibility(View.VISIBLE);
    }
}
