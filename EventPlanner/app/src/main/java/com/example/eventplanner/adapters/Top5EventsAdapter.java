package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.EventDetailsActivity;

import java.util.List;
import java.util.Objects;

public class Top5EventsAdapter extends RecyclerView.Adapter<Top5EventsAdapter.Top5EventsViewHolder> {
    private final Context context;
    private final List<Event> eventList;
    private RecyclerView recyclerView;
    private SnapHelper snapHelper;

    public Top5EventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setSnapHelper(SnapHelper snapHelper) {
        this.snapHelper = snapHelper;
    }

    @NonNull
    @Override
    public Top5EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item_event, parent, false);
        return new Top5EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Top5EventsViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        holder.image.setImageResource(event.getImageResourceId());

        View snapView = snapHelper.findSnapView(recyclerView.getLayoutManager());
        if (snapView != null) {
            int centerPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(snapView);

            float scaleFactor = (position == centerPosition) ? 1.0f : 0.8f;
            holder.itemView.setScaleX(scaleFactor);
            holder.itemView.setScaleY(scaleFactor);
        }

        holder.itemView.setOnClickListener(v -> {
            View currentSnapView = snapHelper.findSnapView(recyclerView.getLayoutManager());
            int currentCenterPosition = currentSnapView != null ? Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(currentSnapView) : -1;

            if (position == currentCenterPosition) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventTitle", event.getTitle());
                intent.putExtra("eventDescription", event.getDescription());
                intent.putExtra("eventImage", event.getImageResourceId());
                context.startActivity(intent);
            } else {
                recyclerView.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
}
