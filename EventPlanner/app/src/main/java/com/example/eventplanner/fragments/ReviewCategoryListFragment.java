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
import com.example.eventplanner.adapters.ReviewCategoryListAdapter;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

public class ReviewCategoryListFragment extends ListFragment {
    private ReviewCategoryListAdapter adapter;
    private CategoryCardViewModel categoriesViewModel;

    public static ReviewCategoryListFragment newInstance() {
        return new ReviewCategoryListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoryCardViewModel.class);

        adapter = new ReviewCategoryListAdapter(requireActivity(), categoriesViewModel);
        setListAdapter(adapter);

        categoriesViewModel.getReviewCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.updateCategoriesList(categories);
        });

        categoriesViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        categoriesViewModel.fetchReviewCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        categoriesViewModel.getReviewCategories();
    }
}
