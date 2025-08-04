package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.NotificationAdapter;
import com.example.eventplanner.viewmodels.CommunicationViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;

public class NotificationsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private CommunicationViewModel communicationViewModel;
    private UserViewModel userViewModel;
    private SwitchCompat muteSwitch;
    private TextView muteStatus;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = getLayoutInflater().inflate(R.layout.activity_notifications, null);
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(contentView);

        recyclerView = findViewById(R.id.notifications_recycler);
        muteSwitch = findViewById(R.id.mute_switch);
        muteStatus = findViewById(R.id.mute_status);
        emptyMessage = findViewById(R.id.empty_message);

        communicationViewModel = CommunicationViewModel.getInstance();

        adapter = new NotificationAdapter(this, communicationViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        communicationViewModel.getNotificationsLiveData().observe(this, notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.GONE);
                adapter.submitList(notifications);
            }
        });

        userViewModel = new UserViewModel();
        userViewModel.setContext(getApplicationContext());
        userViewModel.fetchLoggedUser();

        userViewModel.getLoggedUser().observe(this, user -> {
            if (user != null) {
                boolean isMuted = "DISABLED".equals(user.getNotificationStatus());

                muteSwitch.setOnCheckedChangeListener(null);
                muteSwitch.setChecked(isMuted);
                muteStatus.setText(isMuted ? "Muted" : "Unmuted");

                muteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    muteSwitch.setEnabled(false);

                    muteStatus.setText(isChecked ? "Muted" : "Unmuted");
                    Toast.makeText(this, isChecked ? "Notifications muted" : "Notifications unmuted", Toast.LENGTH_SHORT).show();

                    communicationViewModel.toggleNotifications();

                    muteSwitch.postDelayed(() -> muteSwitch.setEnabled(true), 1000);
                });
            }
        });
    }
}
