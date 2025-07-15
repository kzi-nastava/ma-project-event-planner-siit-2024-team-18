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
import com.example.eventplanner.fragments.ProductFilter;
import com.example.eventplanner.fragments.ProductListFragment;
import com.example.eventplanner.viewmodels.ProductListViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AllProductsActivity extends BaseActivity implements FilterApplyListener {
    private TextInputEditText searchEditText;
    private ImageView addProductButton, btnBack, btnFilter;
    private FrameLayout btnSearch;
    private ProductListFragment productListFragment;
    private ProductListViewModel productListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_products, findViewById(R.id.content_frame));

        initializeViews();
        initializeProductsFragment();
        setupUIInteractions();
    }

    @Override
    public void onFilterApplied(String category, String eventType, int minPrice, int maxPrice, String isAvailable) {
        productListViewModel.setCategoryFilter(category);
        productListViewModel.setEventTypeFilter(eventType);
        productListViewModel.setPriceRange(minPrice, maxPrice);
        productListViewModel.setAvailabilityFilter(isAvailable);
        productListViewModel.fetchProducts();
    }

    private void initializeViews() {
        productListViewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
        productListViewModel.setContext(this);
        addProductButton = findViewById(R.id.addProduct);
        searchEditText = findViewById(R.id.searchEditText);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void setupUIInteractions() {
        setupSearch();
        setupAddProductButton();
        setupBackButton();
        setupFilterButton();
    }

    private void initializeProductsFragment() {
        productListFragment = ProductListFragment.newInstance();
        productListViewModel = new ViewModelProvider(this).get(ProductListViewModel.class);

        FragmentTransition.to(productListFragment, this, false, R.id.listViewProducts);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            hideKeyboard(v);
            productListViewModel.setNameFilter(query);
            productListViewModel.fetchProducts();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    productListViewModel.setNameFilter("");
                    productListViewModel.fetchProducts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupAddProductButton() {
        addProductButton.setOnClickListener(v -> {
            Intent createIntent = new Intent(AllProductsActivity.this, CreateProductActivity.class);
            startActivity(createIntent);
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(view -> {
            new ProductFilter().show(getSupportFragmentManager(), "FilterBottomSheet");
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
