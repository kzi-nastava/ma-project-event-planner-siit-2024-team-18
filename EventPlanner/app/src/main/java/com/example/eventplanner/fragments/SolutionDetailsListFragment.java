package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.SolutionDetailsListAdapter;
import com.example.eventplanner.viewmodels.BudgetViewModel;

public class SolutionDetailsListFragment extends ListFragment {
    private SolutionDetailsListAdapter adapter;
    private BudgetViewModel budgetViewModel;

    public static SolutionDetailsListFragment newInstance() {
        return new SolutionDetailsListFragment();
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

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        adapter = new SolutionDetailsListAdapter(requireActivity(), budgetViewModel);
        setListAdapter(adapter);

        budgetViewModel.getSolutionDetails().observe(getViewLifecycleOwner(), products -> {
            adapter.updateSolutionDetailsList(products);
        });

        budgetViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        budgetViewModel.fetchSolutionDetails(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        budgetViewModel.getSolutionDetails();
    }
}
