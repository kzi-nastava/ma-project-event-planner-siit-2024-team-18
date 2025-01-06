package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.PricelistListFragment;
import com.example.eventplanner.viewmodels.PricelistViewModel;

public class PricelistActivity extends BaseActivity {
    private ImageView createPDFButton, btnBack;
    private PricelistViewModel pricelistViewModel;
    private PricelistListFragment pricelistListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_pricelist, findViewById(R.id.content_frame));

        initializeViews();
        initializePricelistFragment();
        setupListeners();
    }

    private void initializeViews() {
        pricelistViewModel = new ViewModelProvider(this).get(PricelistViewModel.class);
        pricelistViewModel.setContext(this);

        createPDFButton = findViewById(R.id.createPDF);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializePricelistFragment() {
        pricelistListFragment = pricelistListFragment.newInstance(pricelistViewModel);
        FragmentTransition.to(pricelistListFragment, this, false, R.id.listViewPricelistItems);
    }

    private void setupListeners() {
        setupAddpricelistButton();
        setupBackButton();
    }

    private void setupAddpricelistButton() {
        createPDFButton.setOnClickListener(v -> {
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }
}
