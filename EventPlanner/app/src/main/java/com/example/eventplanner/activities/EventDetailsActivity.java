package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventplanner.R;

public class EventDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_event_details, findViewById(R.id.content_frame));

        ImageView eventImage = findViewById(R.id.event_image);
        TextView eventTitle = findViewById(R.id.event_title);
        TextView eventDescription = findViewById(R.id.event_description);

        String title = getIntent().getStringExtra("eventTitle");
        String description = getIntent().getStringExtra("eventDescription");
        int imageResource = getIntent().getIntExtra("eventImage", R.drawable.event_placeholder);

        eventImage.setImageResource(imageResource);
        eventTitle.setText(title);
        eventDescription.setText(description);
    }
}
