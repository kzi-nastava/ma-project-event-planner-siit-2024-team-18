package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.filters.ServiceFilter;
import com.example.eventplanner.fragments.FragmentFilter;
import com.example.eventplanner.fragments.ServiceListFragment;
import com.example.eventplanner.models.Service;
import com.google.android.material.textfield.TextInputEditText;

import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AllServicesActivity extends BaseActivity {

    private TextInputEditText searchEditText;
    private ImageView addServiceButton, btnBack, btnFilter;
    private FrameLayout btnSearch;
    private ServiceListFragment serviceListFragment;
    private ServiceFilter serviceFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_services, findViewById(R.id.content_frame));

        // Initialize views
        initializeViews();

        // Load and display the list of services
        initializeServicesFragment();

        // Setup UI interactions
        setupSearch();
        setupAddServiceButton();
        setupBackButton();
        setupFilterButton();
    }

    private void initializeViews() {
        addServiceButton = findViewById(R.id.addService);
        searchEditText = findViewById(R.id.searchEditText);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void initializeServicesFragment() {
        FragmentTransition.to(ServiceListFragment.newInstance(), this, false, R.id.listViewServices);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (serviceListFragment != null) {
                this.filterServices(query);
            }
            hideKeyboard(v);
        });
    }

    private void setupAddServiceButton() {
        addServiceButton.setOnClickListener(v -> {
            Intent createIntent = new Intent(AllServicesActivity.this, CreateServiceActivity.class);
            startActivity(createIntent);
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(view -> {
            FragmentFilter filterFragment = new FragmentFilter();
            filterFragment.show(getSupportFragmentManager(), "FilterBottomSheet");
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void filterServices(String query) {
        if (serviceFilter != null) {
            serviceFilter.getFilter().filter(query);
        }
    }
}
