package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.eventplanner.R;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.models.ServiceCategory;
import java.util.ArrayList;
import java.util.List;

public class FragmentFilter extends DialogFragment {

    private Spinner categorySpinner, eventTypeSpinner;
    private List<ServiceCategory> categories;
    private List<EventType> eventTypes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);

        categorySpinner = view.findViewById(R.id.spinnerCategory);
        eventTypeSpinner = view.findViewById(R.id.spinnerEventType);

        // Load categories and event types
        loadCategories();
        loadEventTypes();

        // Set up adapters
        ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        return view;
    }

    private void loadCategories() {
        categories = new ArrayList<>();
        categories.add(new ServiceCategory("Category"));
        categories.add(new ServiceCategory("Food"));
        categories.add(new ServiceCategory("Music"));
        categories.add(new ServiceCategory("Media"));
        categories.add(new ServiceCategory("Venue"));
    }

    private void loadEventTypes() {
        eventTypes = new ArrayList<>();
        eventTypes.add(new EventType("Event Type"));
        eventTypes.add(new EventType("Wedding"));
        eventTypes.add(new EventType("Party"));
        eventTypes.add(new EventType("Birthday"));
        eventTypes.add(new EventType("Conference"));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            // Make the dialog float like an island
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
