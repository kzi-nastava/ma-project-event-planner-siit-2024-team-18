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
import com.example.eventplanner.adapters.CommentListAdapter;
import com.example.eventplanner.viewmodels.ProductDetailsViewModel;

public class CommentListFragment extends ListFragment {
    private int solutionId;
    private CommentListAdapter adapter;
    private ProductDetailsViewModel productsViewModel;

    public static CommentListFragment newInstance(int solutionId) {
        CommentListFragment fragment = new CommentListFragment();
        fragment.setSolutionId(solutionId);
        return fragment;
    }

    public void setSolutionId(int solutionId) {
        this.solutionId = solutionId;
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
        productsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
        productsViewModel.setContext(getContext());

        adapter = new CommentListAdapter(requireActivity());
        setListAdapter(adapter);

        productsViewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments != null) {
                adapter.updateCommentList(comments);
            }
        });

        productsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        productsViewModel.fetchComments(solutionId);
    }

    @Override
    public void onResume() {
        super.onResume();
        productsViewModel.getComments();
    }
}
