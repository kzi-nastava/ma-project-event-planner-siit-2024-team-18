package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.AddCategoryDialog;
import com.example.eventplanner.dialogs.CreateEventTypeDialog;
import com.example.eventplanner.fragments.CategoryListFragment;
import com.example.eventplanner.fragments.EventTypeListFragment;
import com.example.eventplanner.fragments.ReviewCategoriesFragment;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class EventTypesActivity extends BaseActivity {
    private ImageView addEventTypeButton, btnBack;
    private EventTypeCardViewModel eventTypeCardViewModel;
    private CategoryCardViewModel categoryCardViewModel;
    private EventTypeListFragment eventTypeListFragment;
    private final List<String> allCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_event_types, findViewById(R.id.content_frame));

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        eventTypeCardViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeCardViewModel.setContext(this);
        categoryCardViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        categoryCardViewModel.setContext(this);
        categoryCardViewModel.fetchCategories();
        categoryCardViewModel.getCategories().observeForever(categoryList -> {
            allCategories.clear();
            for (Category category : categoryList) {
                allCategories.add(category.getName());
            }
        });
        addEventTypeButton = findViewById(R.id.addEventType);
        btnBack = findViewById(R.id.btnBack);
        eventTypeListFragment = EventTypeListFragment.newInstance();
        FragmentTransition.to(eventTypeListFragment, this, false, R.id.listViewEventTypes);
    }

    private void setupListeners() {
        setupAddEventTypeButton();
        setupBackButton();
    }

    private void setupAddEventTypeButton() {
        addEventTypeButton.setOnClickListener(v -> {
            CreateEventTypeDialog.show(this, allCategories, (name, description, categories) -> {
                eventTypeCardViewModel.createEventType(new EventType(name, description, categories));
            });
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.addEventType).setVisibility(View.GONE);
        findViewById(R.id.btnBack).setVisibility(View.GONE);
        findViewById(R.id.listViewEventTypesContainer).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.addEventType).setVisibility(View.VISIBLE);
        findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
        findViewById(R.id.listViewEventTypesContainer).setVisibility(View.VISIBLE);
    }
}
