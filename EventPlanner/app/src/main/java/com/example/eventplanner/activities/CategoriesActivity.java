package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.AddCategoryDialog;
import com.example.eventplanner.fragments.CategoryListFragment;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

public class CategoriesActivity extends BaseActivity {
    private ImageView addCategoryButton, btnBack;
    private CategoryCardViewModel categoryViewModel;
    private CategoryListFragment categoryListFragment;
    private TextView reviewCategories;

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
        reviewCategories = findViewById(R.id.reviewCategories);
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
        setupReviewCategories();
    }

    private void setupAddCategoryButton() {
        addCategoryButton.setOnClickListener(v -> {
            AddCategoryDialog.show(this, (name, description) -> {
                categoryViewModel.addCategory(new Category(name, description));
            });
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setupReviewCategories() {
        reviewCategories.setOnClickListener(view -> finish());
    }
}
