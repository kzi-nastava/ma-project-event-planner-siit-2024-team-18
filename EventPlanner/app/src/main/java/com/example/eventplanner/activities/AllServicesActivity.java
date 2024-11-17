package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.FragmentFilter;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.adapters.ServiceAdapter;
import com.google.android.material.textfield.TextInputEditText;

import android.view.inputmethod.InputMethodManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AllServicesActivity extends BaseActivity {
    private ListView listViewServices;
    private TextInputEditText searchEditText;
    private ImageView addServiceButton, btnBack, btnFilter;
    private FrameLayout btnSearch;
    private ServiceAdapter serviceAdapter;
    private List<Service> servicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_services, findViewById(R.id.content_frame));

        // Initialize views
        addServiceButton = findViewById(R.id.addService);
        searchEditText = findViewById(R.id.searchEditText);
        listViewServices = findViewById(R.id.listViewServices);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);

        // Load dummy data
        loadServices();

        // Set up the list view adapter
        serviceAdapter = new ServiceAdapter(this, servicesList);
        listViewServices.setAdapter(serviceAdapter);

        // Search functionality
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            serviceAdapter.getFilter().filter(query);
            hideKeyboard(v);
        });

        // Create new service activity
        addServiceButton.setOnClickListener(v -> {
            Intent createIntent = new Intent(AllServicesActivity.this, CreateServiceActivity.class);
            startActivity(createIntent);
        });

        btnBack.setOnClickListener(view -> finish());

        // Open filter bottom sheet
        btnFilter.setOnClickListener(view -> {
            FragmentFilter filterFragment = new FragmentFilter();
            filterFragment.show(getSupportFragmentManager(), "FilterBottomSheet");
        });
    }

    private void loadServices() {
        servicesList = new ArrayList<>();
        servicesList.add(new Service("1", "Catering", "Specifics", "Food", "Wedding", 200, 15, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("2", "DJ", "Specifics", "Music", "Party", 300, 30, true, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
        servicesList.add(new Service("3", "Photography", "Specifics", "Media", "Birthday", 150, 15, false, LocalDateTime.now(), LocalDateTime.now(), 60, "ReservationType"));
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
