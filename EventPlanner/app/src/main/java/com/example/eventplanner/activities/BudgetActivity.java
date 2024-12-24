package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.AddBudgetItemDialog;
import com.example.eventplanner.fragments.BudgetItemListFragment;
import com.example.eventplanner.fragments.SolutionDetailsFragment;
import com.example.eventplanner.models.BudgetItem;
import com.example.eventplanner.viewmodels.BudgetViewModel;

public class BudgetActivity extends BaseActivity implements SolutionDetailsFragment.OnFragmentCloseListener {
    private ImageView addBudgetItemButton, btnBack;
    private BudgetViewModel budgetViewModel;
    private BudgetItemListFragment budgetItemListFragment;
    private TextView solutionDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_budget, findViewById(R.id.content_frame));

        initializeViews();
        initializeBudgetItemsFragment();
        setupListeners();
    }

    @Override
    public void onFragmentClosed() {
        onResume();
    }

    private void initializeViews() {
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        addBudgetItemButton = findViewById(R.id.addBudgetItem);
        solutionDetails = findViewById(R.id.solutionDetails);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeBudgetItemsFragment() {
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetItemListFragment = BudgetItemListFragment.newInstance(budgetViewModel);

        FragmentTransition.to(budgetItemListFragment, this, false, R.id.listViewBudgetItems);
    }

    private void setupListeners() {
        setupAddBudgetItemButton();
        setupBackButton();
        setupSolutionDetails();
    }

    private void setupAddBudgetItemButton() {
        addBudgetItemButton.setOnClickListener(v -> {
            AddBudgetItemDialog.show(this, (category, maxAmount) -> {
                budgetViewModel.addBudgetItem(new BudgetItem(category, Integer.parseInt(maxAmount)));
            });
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setupSolutionDetails() {
        solutionDetails.setOnClickListener(view -> {
            onPause();
            FragmentTransition.to(SolutionDetailsFragment.newInstance(), this, true, R.id.content_frame);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.addBudgetItem).setVisibility(View.GONE);
        findViewById(R.id.btnBack).setVisibility(View.GONE);
        findViewById(R.id.solutionDetails).setVisibility(View.GONE);
        findViewById(R.id.listViewBudgetItemsContainer).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.addBudgetItem).setVisibility(View.VISIBLE);
        findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
        findViewById(R.id.solutionDetails).setVisibility(View.VISIBLE);
        findViewById(R.id.listViewBudgetItemsContainer).setVisibility(View.VISIBLE);
    }
}
