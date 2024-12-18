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
import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.EventDetailsActivity;
import com.example.eventplanner.models.EventCard;

import java.util.ArrayList;
import java.util.List;

public class Top5EventsAdapter extends RecyclerView.Adapter<Top5EventsAdapter.Top5EventsViewHolder> implements Filterable {
    private final Context context;
    private List<EventCard> eventList;
    private List<EventCard> eventListFiltered;

    public Top5EventsAdapter(Context context, List<EventCard> eventList) {
        this.context = context;
        this.eventList = new ArrayList<>(eventList);
        this.eventListFiltered = new ArrayList<>(eventList);
    }

    @NonNull
    @Override
    public Top5EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item_event, parent, false);
        return new Top5EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Top5EventsViewHolder holder, int position) {
        EventCard event = eventListFiltered.get(position);
        holder.title.setText(event.getName());
        holder.description.setText(event.getDescription());

        Glide.with(context)
                .load(event.getCardImage())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventListFiltered.size();
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

    static class Top5EventsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;

        public Top5EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.carousel_item_image);
            title = itemView.findViewById(R.id.carousel_item_title);
            description = itemView.findViewById(R.id.carousel_item_description);
        }
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
}
