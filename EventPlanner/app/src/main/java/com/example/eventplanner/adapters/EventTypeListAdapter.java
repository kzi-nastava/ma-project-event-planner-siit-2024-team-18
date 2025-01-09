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
import com.example.eventplanner.dialogs.EditEventTypeDialog;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class EventTypeListAdapter extends ArrayAdapter<EventType> {
    private LinearLayout eventTypeCard;
    private ArrayList<EventType> eventTypes;
    private EventTypeCardViewModel eventTypeCardViewModel;
    private TextView eventTypeName, eventTypeDescription, eventTypeCategories;
    private FrameLayout frameEditEventType, frameDeleteEventType;
    private final List<String> categories = new ArrayList<>();
    private CategoryCardViewModel categoryCardViewModel;

    public EventTypeListAdapter(Activity context, EventTypeCardViewModel eventTypeCardViewModel, CategoryCardViewModel categoryCardViewModel) {
        super(context, R.layout.event_type_card);
        this.eventTypes = new ArrayList<>();
        this.eventTypeCardViewModel = eventTypeCardViewModel;
        this.categoryCardViewModel = categoryCardViewModel;
        this.categoryCardViewModel.fetchCategories();
        this.categoryCardViewModel.getCategories().observeForever(categoryList -> {
            categories.clear();
            for (Category category : categoryList) {
                categories.add(category.getName());
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_card, parent, false);
        }

        EventType eventType = getItem(position);

        initializeViews(convertView);
        populateFields(eventType);
        setupListeners(eventType);

        return convertView;
    }

    @Override
    public int getCount() {
        return eventTypes.size();
    }

    @Nullable
    @Override
    public EventType getItem(int position) {
        return eventTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        eventTypeCard = convertView.findViewById(R.id.event_type_card);
        eventTypeName = convertView.findViewById(R.id.event_type_name);
        eventTypeDescription = convertView.findViewById(R.id.event_type_description);
        eventTypeCategories = convertView.findViewById(R.id.event_type_categories);
        frameEditEventType = convertView.findViewById(R.id.edit_event_type);
        frameDeleteEventType = convertView.findViewById(R.id.delete_event_type);
    }

    private void populateFields(EventType eventType) {
        eventTypeName.setText(eventType.getName());
        eventTypeDescription.setText(eventType.getDescription());
        StringBuilder sb = new StringBuilder("Categories: ");
        for (String category : eventType.getCategories()) {
            sb.append(category);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        eventTypeCategories.setText(sb.toString());
    }

    private void setupListeners(EventType eventType) {
        frameEditEventType.setOnClickListener(v -> {
            EditEventTypeDialog.show(getContext(), this.categories, eventType, updatedEventType -> {
                notifyDataSetChanged();
                eventTypeCardViewModel.updateEventType(updatedEventType.getId(), updatedEventType);
            });
        });

        frameDeleteEventType.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this event type?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        eventTypeCardViewModel.deleteEventType(eventType.getId());
                        Toast.makeText(v.getContext(), "Event type successfuly deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    public void updateEventTypesList(List<EventType> allEventTypes) {
        if (allEventTypes != null) {
            this.eventTypes.clear();
            this.eventTypes.addAll(allEventTypes);
            notifyDataSetChanged();
        }
    }
}
