package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.activities.details.EventDetailsActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.models.EventCard;

import java.util.ArrayList;
import java.util.List;

public class AllEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    private List<EventCard> eventList;
    private List<EventCard> eventListFiltered;

    public AllEventsAdapter(Context context, List<EventCard> events) {
        this.context = context;
        this.eventList = new ArrayList<>(events);
        this.eventListFiltered = new ArrayList<>(events);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventCard event = eventListFiltered.get(position);
        EventViewHolder eventHolder = (EventViewHolder) holder;
        eventHolder.eventTitle.setText(event.getName());
        eventHolder.eventDescription.setText(event.getDescription());

        Glide.with(context)
                .load(event.getCardImage())
                .into(eventHolder.eventImage);

        eventHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventListFiltered.size();
    }

    public void updateEventList(List<EventCard> events) {
        if (events != null) {
            this.eventList.clear();
            this.eventList.addAll(events);
            this.eventListFiltered.clear();
            this.eventListFiltered.addAll(events);
            notifyDataSetChanged();
        }
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDescription;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventImage = itemView.findViewById(R.id.event_image);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<EventCard> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(eventList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (EventCard event : eventList) {
                        if (event.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(event);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                eventListFiltered.clear();
                eventListFiltered.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
