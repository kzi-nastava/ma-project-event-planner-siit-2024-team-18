package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;

public class EventDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_event_details, findViewById(R.id.content_frame));

        ImageView eventImage = findViewById(R.id.event_image);
        TextView eventTitle = findViewById(R.id.event_title);
        TextView eventDescription = findViewById(R.id.event_description);

        // Get data passed from AllEventsAdapter
        String title = getIntent().getStringExtra("eventTitle");
        String description = getIntent().getStringExtra("eventDescription");
        String imageUrl = getIntent().getStringExtra("eventImage");

        // Set the event details
        eventTitle.setText(title);
        eventDescription.setText(description);

        // Load the image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.event_placeholder) // Optional: show a placeholder while loading
                .error(R.drawable.event_placeholder)       // Optional: show placeholder if loading fails
                .into(eventImage);
    }
}