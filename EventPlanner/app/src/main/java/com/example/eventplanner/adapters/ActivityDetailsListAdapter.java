package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.models.EventActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailsListAdapter extends RecyclerView.Adapter<ActivityDetailsListAdapter.ViewHolder> {
    private List<EventActivity> activities = new ArrayList<>();
    private Activity context;

    public ActivityDetailsListAdapter(Activity context) {
        this.context = context;
    }

    public void updateActivityList(List<EventActivity> allActivities) {
        if (allActivities != null) {
            this.activities.clear();
            this.activities.addAll(allActivities);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ActivityDetailsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_details_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityDetailsListAdapter.ViewHolder holder, int position) {
        EventActivity activity = activities.get(position);
        holder.activityName.setText(activity.getName());
        holder.description.setText(activity.getDescription());
        holder.location.setText(activity.getLocation());
        holder.startDate.setText(activity.getStartDate().toString());
        holder.endDate.setText(activity.getEndDate().toString());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityName, description, location, startDate, endDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activityName);
            description = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
        }
    }
}
