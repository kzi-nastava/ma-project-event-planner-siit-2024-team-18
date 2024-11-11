package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.view.inputmethod.InputMethodManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AllServicesActivity extends AppCompatActivity {
    private ListView listViewServices;
    private TextInputEditText searchEditText;
    private ImageView addServiceButton, btnBack;
    private FrameLayout btnSearch;
    private Spinner categorySpinner, eventTypeSpinner;
    private SwitchCompat switchCompat;
    private ServiceAdapter serviceAdapter;
    private List<Service> servicesList;
    private List<ServiceCategory> categories;
    private List<EventType> eventTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);

        // receiving views
        addServiceButton = findViewById(R.id.addService);
        searchEditText = findViewById(R.id.searchEditText);
        listViewServices = findViewById(R.id.listViewServices);
        btnSearch = findViewById(R.id.btnSearch);
        categorySpinner = findViewById(R.id.spinnerCategory);
        eventTypeSpinner = findViewById(R.id.spinnerEventType);
        switchCompat = findViewById(R.id.switchAvailability);
        btnBack = findViewById(R.id.btnBack);

        // creating dummy data
        loadServices();
        loadCategories();
        loadEventTypes();

        // filling spinners with data
        ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        // putting cards in a list view
        serviceAdapter = new ServiceAdapter(this, servicesList);
        listViewServices.setAdapter(serviceAdapter);

        // public services search enabled
        switchCompat.setChecked(true);

        // search
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            serviceAdapter.getFilter().filter(query);
            hideKeyboard(v);
        });

        // create new service activity
        ImageView createService = addServiceButton.findViewById(R.id.addService);
        createService.setOnClickListener(v -> {
            Intent createIntent = new Intent(AllServicesActivity.this, CreateServiceActivity.class);
            startActivity(createIntent);
        });

        btnBack.setOnClickListener(view -> finish());
    }

    private void loadServices() {
        servicesList = new ArrayList<>();
        servicesList.add(new Service("1", "Catering", "Specifics", "Food", "Wedding", 200, 15, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("2", "DJ", "Specifics", "Music", "Party", 300, 30, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("3", "Photography", "Specifics", "Media", "Birthday", 150, 15, false, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("4", "Decoration", "Specifics", "Venue", "Conference", 400, 15, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("5", "Lighting", "Specifics", "Music", "Party", 600, 15, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("6", "Photography", "Specifics", "Media", "Wedding", 450, 15, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
