package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.EditActivityDialog;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.EventActivity;
import com.example.eventplanner.viewmodels.AgendaViewModel;

import java.util.ArrayList;
import java.util.List;

public class ActivityListAdapter extends ArrayAdapter<EventActivity> {
    private LinearLayout eventActivityCard;
    private ArrayList<EventActivity> activities;
    private AgendaViewModel agendaViewModel;
    private TextView activityName, description, location, startDate, endDate;
    private FrameLayout frameEditActivity, frameDeleteActivity;
    private int eventId;

    public ActivityListAdapter(Activity context, AgendaViewModel agendaViewModel, int eventId) {
        super(context, R.layout.budget_item_card);
        this.activities = new ArrayList<>();
        this.agendaViewModel = agendaViewModel;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_activity_card, parent, false);
        }

        EventActivity activity = getItem(position);

        initializeViews(convertView);
        populateFields(activity);
        setupListeners(activity);

        return convertView;
    }

    @Override
    public int getCount() {
        return activities.size();
    }

    @Nullable
    @Override
    public EventActivity getItem(int position) {
        return activities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        eventActivityCard = convertView.findViewById(R.id.eventActivityCard);
        activityName = convertView.findViewById(R.id.activityName);
        description = convertView.findViewById(R.id.description);
        location = convertView.findViewById(R.id.location);
        startDate = convertView.findViewById(R.id.startDate);
        endDate = convertView.findViewById(R.id.endDate);
        frameEditActivity = convertView.findViewById(R.id.editActivity);
        frameDeleteActivity = convertView.findViewById(R.id.deleteActivity);
    }

    private void populateFields(EventActivity activity) {
        activityName.setText(activity.getName());
        description.setText(activity.getDescription());
        location.setText(activity.getLocation());
        startDate.setText(activity.getStartDate().toString());
        endDate.setText(activity.getEndDate().toString());
    }

    private void setupListeners(EventActivity activity) {
        frameEditActivity.setOnClickListener(v -> {
            EditActivityDialog.show(getContext(), activity, updatedActivity -> {
                agendaViewModel.editActivity(eventId, updatedActivity.getId(), updatedActivity);
            });
        });

        frameDeleteActivity.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this event activity?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        agendaViewModel.deleteActivityById(eventId, activity.getId());
                        Toast.makeText(v.getContext(), "Activity deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    public void updateActivityList(List<EventActivity> allActivities) {
        if (allActivities != null) {
            this.activities.clear();
            this.activities.addAll(allActivities);
            notifyDataSetChanged();
        }
    }
}
