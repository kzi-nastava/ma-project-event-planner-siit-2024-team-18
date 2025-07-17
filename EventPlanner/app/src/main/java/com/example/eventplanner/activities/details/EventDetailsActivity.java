package com.example.eventplanner.activities.details;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.fragments.ContentUnavailableFragment;
import com.example.eventplanner.fragments.InviteScreenFragment;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.viewmodels.EventDetailsViewModel;
import com.example.eventplanner.viewmodels.LoginViewModel;

import java.util.Objects;

public class EventDetailsActivity extends BaseActivity {

    private EventDetailsViewModel eventDetailsViewModel;
    private LoginViewModel loginViewModel;

    private ImageView eventImage;
    private TextView eventTitle, eventDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_event_details, findViewById(R.id.content_frame));
        loginViewModel = LoginViewModel.getInstance(getApplicationContext());

        eventImage = findViewById(R.id.event_image);
        eventTitle = findViewById(R.id.event_title);
        eventDescription = findViewById(R.id.event_description);

        int eventId = getIntent().getIntExtra("eventId", -1);

        if (eventId != -1) {
            setupViewModel();
            eventDetailsViewModel.fetchEventDetailsById(eventId);
        } else {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewModel() {
        eventDetailsViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        eventDetailsViewModel.setContext(this);

        eventDetailsViewModel.getEventDetails().observe(this, this::displayEventDetails);

        eventDetailsViewModel.isLoading().observe(this, isLoading -> {
            // Handle loading state
        });

        eventDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.startsWith("Network error: End of input at line 1 column 1 path $")) {
                    navigateToUnavailableContentFragment();
                } else {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayEventDetails(Event event) {
        if (event != null) {
            eventTitle.setText(event.getName());
            eventDescription.setText(event.getDescription());

            if (event.getImages() != null && event.getImages().length > 0) {
                Glide.with(this)
                        .load(event.getImages()[0])
                        .placeholder(R.drawable.event_placeholder)
                        .error(R.drawable.event_placeholder)
                        .into(eventImage);
            } else {
                eventImage.setImageResource(R.drawable.event_placeholder);
            }
        }
    }

    private void navigateToUnavailableContentFragment() {
        ContentUnavailableFragment fragment = new ContentUnavailableFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
