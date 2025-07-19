package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.BudgetDetailsListAdapter;
import com.example.eventplanner.viewmodels.BudgetViewModel;

public class BudgetDetailsListFragment extends Fragment {
    private BudgetDetailsListAdapter adapter;
    private BudgetViewModel budgetViewModel;
    private int eventId;
    private TextView budget, noBudget;

    public static BudgetDetailsListFragment newInstance(BudgetViewModel budgetViewModel, int eventId) {
        BudgetDetailsListFragment fragment = new BudgetDetailsListFragment();
        fragment.setAgendaViewModelAndEvent(budgetViewModel, eventId);
        return fragment;
    }

    public void setAgendaViewModelAndEvent(BudgetViewModel budgetViewModel, int eventId) {
        this.budgetViewModel = budgetViewModel;
        this.eventId = eventId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget_details_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewBudget);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new BudgetDetailsListAdapter(requireActivity());
        recyclerView.setAdapter(adapter);

        budget = view.findViewById(R.id.budget);
        noBudget = view.findViewById(R.id.noBudget);

        budgetViewModel.getBudgetItems().observe(getViewLifecycleOwner(), budgetItems -> {
            if (budgetItems != null && !budgetItems.isEmpty()) {
                adapter.updateBudgetItemList(budgetItems);
                budget.setVisibility(View.VISIBLE);
                noBudget.setVisibility(View.GONE);
            } else {
                budget.setVisibility(View.GONE);
                noBudget.setVisibility(View.VISIBLE);
            }
        });

        budgetViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        budgetViewModel.fetchBudgetItems(eventId);
    }

    @Override
    public void onResume() {
        super.onResume();
        budgetViewModel.getBudgetItems();
    }
}
