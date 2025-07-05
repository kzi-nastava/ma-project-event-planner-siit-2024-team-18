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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AllProductsServicesAdapter;
import com.example.eventplanner.viewmodels.AllProductsServicesViewModel;

import java.util.ArrayList;

public class FavouriteSolutionsListFragment extends Fragment {
    private AllProductsServicesAdapter adapter;
    private AllProductsServicesViewModel solutionsViewModel;
    private TextView noSolutionsTextView;

    public static FavouriteSolutionsListFragment newInstance() {
        return new FavouriteSolutionsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_solutions_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AllProductsServicesAdapter(requireActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        solutionsViewModel = new ViewModelProvider(requireActivity()).get(AllProductsServicesViewModel.class);
        solutionsViewModel.setContext(requireContext());
        noSolutionsTextView = view.findViewById(R.id.noSolutionsTextView);

        solutionsViewModel.getFavouriteSolutions().observe(getViewLifecycleOwner(), favouriteSolutions -> {
            if (favouriteSolutions != null && !favouriteSolutions.isEmpty()) {
                adapter.updateSolutionCardList(favouriteSolutions);
                noSolutionsTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                noSolutionsTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        solutionsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        solutionsViewModel.fetchFavouriteSolutions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        solutionsViewModel.getFavouriteSolutions();
    }
}
