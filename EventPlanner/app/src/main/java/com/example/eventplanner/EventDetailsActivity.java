package com.example.eventplanner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

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
