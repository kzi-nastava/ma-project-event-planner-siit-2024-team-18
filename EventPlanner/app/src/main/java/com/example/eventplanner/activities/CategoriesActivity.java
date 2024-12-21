package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.CategoryListFragment;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

public class CategoriesActivity extends BaseActivity {
    private ImageView addCategoryButton, btnBack;
    private CategoryCardViewModel categoryViewModel;
    private CategoryListFragment categoryListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_categories, findViewById(R.id.content_frame));

        initializeViews();
        initializeCategoriesFragment();
        setupListeners();
    }

    private void initializeViews() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        addCategoryButton = findViewById(R.id.addCategory);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeCategoriesFragment() {
        categoryListFragment = CategoryListFragment.newInstance();
        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);

        FragmentTransition.to(categoryListFragment, this, false, R.id.listViewCategories);
    }

    private void setupListeners() {
        setupAddCategoryButton();
        setupBackButton();
    }

    private void setupAddCategoryButton() {
        addCategoryButton.setOnClickListener(v -> {
            Intent createIntent = new Intent(CategoriesActivity.this, CreateServiceActivity.class);
            startActivity(createIntent);
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }
}
