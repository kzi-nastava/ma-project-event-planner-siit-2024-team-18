package com.example.eventplanner.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.EditEventActivity;
import com.example.eventplanner.activities.details.EventDetailsActivity;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.viewmodels.EventListViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends ArrayAdapter<EventCard> {
    private LinearLayout eventCard;
    private ArrayList<EventCard> events;
    private Activity activity;
    private EventListViewModel eventListViewModel;
    private TextView eventName, eventDescription;
    private ImageView imgEvent;
    private FrameLayout frameEditEvent, frameDeleteEvent;
    public EventListAdapter(Activity context, EventListViewModel eventListViewModel) {
        super(context, R.layout.event_card);
        this.events = new ArrayList<>();
        this.activity = context;
        this.eventListViewModel = eventListViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_card, parent, false);
        }

        EventCard event = getItem(position);

        initializeViews(convertView);
        populateFields(event);
        setupListeners(event);

        return convertView;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Nullable
    @Override
    public EventCard getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        eventCard = convertView.findViewById(R.id.eventCard);
        eventName = convertView.findViewById(R.id.txtEventName);
        eventDescription = convertView.findViewById(R.id.txtEventDescription);
        imgEvent = convertView.findViewById(R.id.imgEvent);
        frameEditEvent = convertView.findViewById(R.id.editEvent);
        frameDeleteEvent = convertView.findViewById(R.id.deleteEvent);
    }

    private void populateFields(EventCard event) {
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());

        String imageUrl = event.getCardImage();
        Glide.with(activity)
                .load(imageUrl)
                .into(imgEvent);
    }

    private void setupListeners(EventCard event) {
        frameEditEvent.setOnClickListener(v -> {
            if (event.getStartDate().isAfter(LocalDateTime.now())) {
                Intent editIntent = new Intent(getContext(), EditEventActivity.class);
                editIntent.putExtra("eventId", event.getId());
                getContext().startActivity(editIntent);
            } else {
                Toast.makeText(getContext().getApplicationContext(), "You cannot edit event that has ended!", Toast.LENGTH_SHORT).show();
            }
        });

        frameDeleteEvent.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        eventListViewModel.deleteEventById(event.getId());
                        Toast.makeText(v.getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        eventCard.setOnClickListener(v -> {
            Intent detailIntent = new Intent(activity, EventDetailsActivity.class);
            detailIntent.putExtra("solutionId", event.getId());
            activity.startActivity(detailIntent);
        });
    }

    public void updateEventsList(List<EventCard> events) {
        if (events != null) {
            this.events.clear();
            this.events.addAll(events);
            notifyDataSetChanged();
        }
    }
}
