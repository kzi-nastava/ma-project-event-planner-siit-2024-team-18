package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.FilterApplyListener;
import com.example.eventplanner.fragments.ServiceFilter;
import com.example.eventplanner.fragments.ServiceListFragment;
import com.example.eventplanner.viewmodels.ServiceListViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AllServicesActivity extends BaseActivity implements FilterApplyListener {
    private TextInputEditText searchEditText;
    private ImageView addServiceButton, btnBack, btnFilter;
    private FrameLayout btnSearch;
    private ServiceListFragment serviceListFragment;
    private ServiceListViewModel serviceListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_services, findViewById(R.id.content_frame));

        initializeViews();
        initializeServicesFragment();
        setupUIInteractions();
    }

    @Override
    public void onFilterApplied(String category, String eventType, int minPrice, int maxPrice, String isAvailable) {
        serviceListViewModel.setCategoryFilter(category);
        serviceListViewModel.setEventTypeFilter(eventType);
        serviceListViewModel.setPriceRange(minPrice, maxPrice);
        serviceListViewModel.setAvailabilityFilter(isAvailable);
        serviceListViewModel.fetchServices();
    }

    private void initializeViews() {
        serviceListViewModel = new ViewModelProvider(this).get(ServiceListViewModel.class);
        serviceListViewModel.setContext(this);
        addServiceButton = findViewById(R.id.addService);
        searchEditText = findViewById(R.id.searchEditText);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void setupUIInteractions() {
        setupSearch();
        setupAddServiceButton();
        setupBackButton();
        setupFilterButton();
    }

    private void initializeServicesFragment() {
        serviceListFragment = ServiceListFragment.newInstance();
        serviceListViewModel = new ViewModelProvider(this).get(ServiceListViewModel.class);

        FragmentTransition.to(serviceListFragment, this, false, R.id.listViewServices);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            hideKeyboard(v);
            serviceListViewModel.setNameFilter(query);
            serviceListViewModel.fetchServices();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    serviceListViewModel.setNameFilter("");
                    serviceListViewModel.fetchServices();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
            new ServiceFilter().show(getSupportFragmentManager(), "FilterBottomSheet");
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
