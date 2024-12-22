package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

public class ReviewCategoriesFragment extends Fragment {
    private ImageView btnBack;
    private CategoryCardViewModel categoryViewModel;

    public static ReviewCategoriesFragment newInstance() {
        return new ReviewCategoriesFragment();
    }

    public interface OnFragmentCloseListener {
        void onFragmentClosed();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeFragment(view);
        setupListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof OnFragmentCloseListener) {
            ((OnFragmentCloseListener) getActivity()).onFragmentClosed();
        }
    }

    private void initializeViews(View view) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void initializeFragment(View view) {
        FragmentTransition.to(ReviewCategoryListFragment.newInstance(), requireActivity(), false, R.id.listViewCategoriesInReview);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(view -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
