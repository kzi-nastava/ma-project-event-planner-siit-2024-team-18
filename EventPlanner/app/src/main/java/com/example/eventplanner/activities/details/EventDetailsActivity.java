package com.example.eventplanner.activities.details;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.fragments.InviteScreenFragment;
import com.example.eventplanner.models.Event;

public class EventDetailsActivity extends BaseActivity {

    private EventDetailsViewModel eventDetailsViewModel;

    private ImageView eventImage;
    private TextView eventTitle, eventDescription;
    private Button sendInvitationsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_event_details, findViewById(R.id.content_frame));

        eventImage = findViewById(R.id.event_image);
        eventTitle = findViewById(R.id.event_title);
        eventDescription = findViewById(R.id.event_description);
        sendInvitationsButton = findViewById(R.id.send_invitations_button);

        int eventId = getIntent().getIntExtra("eventId", -1);

        if (eventId != -1) {
            setupViewModel();
            eventDetailsViewModel.fetchEventDetailsById(eventId);
        } else {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
        }

        setupListeners(eventId);
    }

    private void setupViewModel() {
        eventDetailsViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);

        eventDetailsViewModel.getEventDetails().observe(this, this::displayEventDetails);

        eventDetailsViewModel.isLoading().observe(this, isLoading -> {
            // Handle loading state
        });

        eventDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEventDetails(Event event) {
        if (event != null) {
            eventTitle.setText(event.getName());
            eventDescription.setText(event.getDescription());

            if (event.getImages() != null && !event.getImages().isEmpty()) {
                Glide.with(this)
                        .load(event.getImages().get(0))
                        .placeholder(R.drawable.event_placeholder)
                        .error(R.drawable.event_placeholder)
                        .into(eventImage);
            } else {
                eventImage.setImageResource(R.drawable.event_placeholder);
            }
        }
    }

    private void setupListeners(int eventId) {
        sendInvitationsButton.setOnClickListener(v -> navigateToInvitationFragment(eventId));
    }

    private void navigateToInvitationFragment(int eventId) {
        InviteScreenFragment fragment = new InviteScreenFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
