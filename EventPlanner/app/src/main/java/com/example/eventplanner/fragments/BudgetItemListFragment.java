package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.BudgetItemListAdapter;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.viewmodels.BudgetViewModel;

public class BudgetItemListFragment extends ListFragment {
    private BudgetItemListAdapter adapter;
    private BudgetViewModel budgetViewModel;
    private int eventId;

    public static BudgetItemListFragment newInstance(BudgetViewModel budgetViewModel, int eventId) {
        BudgetItemListFragment fragment = new BudgetItemListFragment();
        fragment.setBudgetViewModelAndEvent(budgetViewModel, eventId);
        return fragment;
    }

    public void setBudgetViewModelAndEvent(BudgetViewModel budgetViewModel, int eventId) {
        this.budgetViewModel = budgetViewModel;
        this.eventId = eventId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BudgetItemListAdapter(requireActivity(), budgetViewModel, eventId);
        setListAdapter(adapter);

        budgetViewModel.getBudgetItems().observe(getViewLifecycleOwner(), budgetItems -> {
            if (budgetItems != null) {
                adapter.updateBudgetItemList(budgetItems);
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
