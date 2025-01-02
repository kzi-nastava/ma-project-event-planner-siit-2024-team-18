package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class ServiceFilter extends DialogFragment {
    private Spinner categorySpinner, eventTypeSpinner;
    private EditText minPriceEditText, maxPriceEditText;
    private SwitchCompat availabilitySwitch;
    private Button applyButton, resetButton;
    private List<Category> listCategories = new ArrayList<>();
    private List<EventType> listEventTypes = new ArrayList<>();
    private EventTypeCardViewModel eventTypeViewModel;
    private CategoryCardViewModel categoryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);

        initializeViews(view);
        loadViewModels();
        setupAvailability();
        setupApplyButton();
        setupResetButton();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void initializeViews(View view) {
        categorySpinner = view.findViewById(R.id.spinnerCategory);
        eventTypeSpinner = view.findViewById(R.id.spinnerEventType);
        minPriceEditText = view.findViewById(R.id.minPriceEditText);
        maxPriceEditText = view.findViewById(R.id.maxPriceEditText);
        availabilitySwitch = view.findViewById(R.id.availabilitySwitch);
        resetButton = view.findViewById(R.id.resetButton);
        applyButton = view.findViewById(R.id.applyButton);
    }

    private void loadViewModels() {
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.setContext(requireContext());
        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        categoryViewModel.setContext(requireContext());

        eventTypeViewModel.fetchEventTypes();
        categoryViewModel.fetchCategories();

        eventTypeViewModel.getEventTypes().observe(this, eventTypes -> {
            if (eventTypes != null) {
                this.listEventTypes = eventTypes;
                setupEventTypeAdapter();
            }
        });

        categoryViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                this.listCategories = categories;
                setupCategoryAdapter();
            }
        });
    }

    private void setupCategoryAdapter() {
        List<Category> categoriesWithPlaceholder = new ArrayList<>();
        categoriesWithPlaceholder.add(new Category("Select a category", "Blank"));
        categoriesWithPlaceholder.addAll(listCategories);

        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoriesWithPlaceholder);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setSelection(0);
    }

    private void setupEventTypeAdapter() {
        List<EventType> eventTypesWithPlaceholder = new ArrayList<>();
        eventTypesWithPlaceholder.add(new EventType("Select an event type"));
        eventTypesWithPlaceholder.addAll(listEventTypes);

        ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, eventTypesWithPlaceholder);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        eventTypeSpinner.setSelection(0);
    }

    private void setupAvailability() {
        availabilitySwitch.setChecked(true);
    }

    private void setupApplyButton() {
        applyButton.setOnClickListener(v -> {
            String selectedCategory = ((Category) categorySpinner.getSelectedItem()).getName();
            if (selectedCategory.equals("Select a category")) {
                selectedCategory = "";
            } else {
                selectedCategory = categorySpinner.getSelectedItem().toString();
            }
            String selectedEventType = ((EventType) eventTypeSpinner.getSelectedItem()).getName();
            if (selectedEventType.equals("Select an event type")) {
                selectedEventType = "";
            } else {
                selectedEventType = eventTypeSpinner.getSelectedItem().toString();
            }
            int minPrice = minPriceEditText.getText().toString().isEmpty() ? 0 : Integer.parseInt(minPriceEditText.getText().toString());
            int maxPrice = maxPriceEditText.getText().toString().isEmpty() ? 0 : Integer.parseInt(maxPriceEditText.getText().toString());
            String isAvailable = availabilitySwitch.isChecked() ? "true" : "false";

            FilterApplyListener listener = (FilterApplyListener) getActivity();
            if (listener != null) {
                listener.onFilterApplied(selectedCategory, selectedEventType, minPrice, maxPrice, isAvailable);
            }
            dismiss();
        });
    }

    private void setupResetButton() {
        resetButton.setOnClickListener(v -> {
            String selectedCategory = "";
            String selectedEventType = "";
            int minPrice = 0;
            int maxPrice = 0;
            String isAvailable = "true";

            FilterApplyListener listener = (FilterApplyListener) getActivity();
            if (listener != null) {
                listener.onFilterApplied(selectedCategory, selectedEventType, minPrice, maxPrice, isAvailable);
            }
            dismiss();
        });
    }
}
