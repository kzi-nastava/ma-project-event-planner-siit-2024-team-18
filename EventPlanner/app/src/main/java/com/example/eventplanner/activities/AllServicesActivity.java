package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
import java.util.ArrayList;
import java.util.List;

public class AllServicesActivity extends BaseActivity {

    private TextInputEditText searchEditText;
    private ImageView addServiceButton, btnBack, btnFilter;
    private FrameLayout btnSearch;
    private List<Service> servicesList;
    private ServiceListFragment serviceListFragment;
    private ServiceFilter serviceFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_services, findViewById(R.id.content_frame));

        // Initialize views
        initializeViews();

        // Load and display the list of services
        loadServices();
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
        serviceListFragment = ServiceListFragment.newInstance(new ArrayList<>(servicesList));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listViewServices, serviceListFragment);
        fragmentTransaction.commit();
    }

    private void loadServices() {
        servicesList = new ArrayList<>();
        servicesList.add(new Service("1","Catering","Delicious food and beverages tailored to your event.",500.0,10.0,new String[]{"catering1.jpg", "catering2.jpg"},true,true,"Food",new String[]{"Wedding", "Corporate"},"New York City","user123",false,"Active","Full-Service","Includes wait staff, setup, and cleanup.",120,50,200,30,7));
        servicesList.add(new Service("2","DJ","Professional DJ with a wide selection of music.",300.0,5.0,new String[]{"dj1.jpg", "dj2.jpg"},true,true,"Music",new String[]{"Party", "Wedding"},"Los Angeles","user456",false,"Active","Hourly","Includes sound system and lighting setup.",180,20,100,15,5));
        servicesList.add(new Service("3","Photography","Capture your special moments with our professional photographer.",250.0,15.0,new String[]{"photo1.jpg", "photo2.jpg"},true,false,"Media",new String[]{"Wedding", "Birthday"},"Chicago","user789",false,"Inactive","Package","Includes 100 edited photos and a photo album.",240,10,50,20,10));
        servicesList.add(new Service("4","Event Planning","Expert planners to organize your event from start to finish.",1000.0,20.0,new String[]{"planning1.jpg", "planning2.jpg"},true,true,"Planning",new String[]{"Corporate", "Wedding", "Birthday"},"Houston","user101",false,"Active","Full-Service","Customized event planning tailored to your needs.",480,5,500,60,30));
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
