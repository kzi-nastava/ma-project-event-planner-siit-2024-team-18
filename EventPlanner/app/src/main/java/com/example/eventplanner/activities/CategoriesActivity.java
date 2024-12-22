package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.AddCategoryDialog;
import com.example.eventplanner.fragments.CategoryListFragment;
import com.example.eventplanner.fragments.ReviewCategoriesFragment;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

public class CategoriesActivity extends BaseActivity implements ReviewCategoriesFragment.OnFragmentCloseListener {
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

    @Override
    public void onFragmentClosed() {
        onResume();
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
        reviewCategories.setOnClickListener(view -> {
            onPause();
            FragmentTransition.to(ReviewCategoriesFragment.newInstance(), this, true, R.id.content_frame);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.addCategory).setVisibility(View.GONE);
        findViewById(R.id.btnBack).setVisibility(View.GONE);
        findViewById(R.id.reviewCategories).setVisibility(View.GONE);
        findViewById(R.id.listViewCategoriesContainer).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.addCategory).setVisibility(View.VISIBLE);
        findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
        findViewById(R.id.reviewCategories).setVisibility(View.VISIBLE);
        findViewById(R.id.listViewCategoriesContainer).setVisibility(View.VISIBLE);
    }
}
