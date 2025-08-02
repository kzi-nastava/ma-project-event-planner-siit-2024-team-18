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
import com.example.eventplanner.models.Event;
import com.example.eventplanner.viewmodels.BudgetViewModel;

public class BudgetActivity extends BaseActivity implements SolutionDetailsFragment.OnFragmentCloseListener {
    private ImageView addBudgetItemButton, btnBack;
    private BudgetViewModel budgetViewModel;
    private BudgetItemListFragment budgetItemListFragment;
    private TextView solutionDetails, totalBudget;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_budget, findViewById(R.id.content_frame));

        eventId = getIntent().getIntExtra("eventId", -1);
        initializeViews();
        initializeBudgetItemsFragment();
        populateData();
        setupListeners();
    }

    @Override
    public void onFragmentClosed() {
        onResume();
    }

    private void initializeViews() {
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetViewModel.setContext(this);
        budgetViewModel.getTotalBudget(eventId);

        addBudgetItemButton = findViewById(R.id.addBudgetItem);
        solutionDetails = findViewById(R.id.solutionDetails);
        totalBudget = findViewById(R.id.totalBudget);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeBudgetItemsFragment() {
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetViewModel.setContext(this);
        budgetItemListFragment = BudgetItemListFragment.newInstance(budgetViewModel, eventId);

        FragmentTransition.to(budgetItemListFragment, this, false, R.id.listViewBudgetItems);
    }

    private void populateData() {
        budgetViewModel.getTotalBudgetLiveData().observe(this, total -> {
            if (total != null) {
                totalBudget.setText(String.format("Total Budget: %d$", total));
            } else {
                totalBudget.setText("Total Budget: N/A");
            }
        });
    }

    private void setupListeners() {
        setupAddBudgetItemButton();
        setupBackButton();
        setupSolutionDetails();
    }

    private void setupAddBudgetItemButton() {
        addBudgetItemButton.setOnClickListener(v -> {
            AddBudgetItemDialog.show(eventId, this, (category, maxAmount) -> {
                budgetViewModel.addBudgetItem(eventId, new BudgetItem(category, Integer.parseInt(maxAmount)));
            });
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setupSolutionDetails() {
        solutionDetails.setOnClickListener(view -> {
            onPause();
            FragmentTransition.to(SolutionDetailsFragment.newInstance(eventId), this, true, R.id.content_frame);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.addBudgetItem).setVisibility(View.GONE);
        findViewById(R.id.btnBack).setVisibility(View.GONE);
        findViewById(R.id.solutionDetails).setVisibility(View.GONE);
        findViewById(R.id.listViewBudgetItemsContainer).setVisibility(View.GONE);
        findViewById(R.id.totalBudget).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.addBudgetItem).setVisibility(View.VISIBLE);
        findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
        findViewById(R.id.solutionDetails).setVisibility(View.VISIBLE);
        findViewById(R.id.listViewBudgetItemsContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.totalBudget).setVisibility(View.VISIBLE);
    }
}
