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
import com.example.eventplanner.viewmodels.BudgetViewModel;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

public class SolutionDetailsFragment extends Fragment {
    private ImageView btnBack;
    private BudgetViewModel budgetViewModel;
    private int eventId;

    public static SolutionDetailsFragment newInstance(int eventId) {
        SolutionDetailsFragment fragment = new SolutionDetailsFragment();
        fragment.setEventId(eventId);

        return fragment;
    }

    public interface OnFragmentCloseListener {
        void onFragmentClosed();
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solution_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeFragment();
        setupListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof SolutionDetailsFragment.OnFragmentCloseListener) {
            ((SolutionDetailsFragment.OnFragmentCloseListener) getActivity()).onFragmentClosed();
        }
    }

    private void initializeViews(View view) {
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetViewModel.setContext(requireContext());
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void initializeFragment() {
        FragmentTransition.to(SolutionDetailsListFragment.newInstance(eventId), requireActivity(), false, R.id.listViewSolutionDetails);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(view -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
