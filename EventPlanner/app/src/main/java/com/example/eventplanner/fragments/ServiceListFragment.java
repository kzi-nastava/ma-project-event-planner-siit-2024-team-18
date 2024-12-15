package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ServiceListAdapter;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.ServiceListViewModel;

import java.util.ArrayList;

public class ServiceListFragment extends ListFragment {

    private ServiceListAdapter adapter;
    private static final String ARG_PARAM = "services";
    private ArrayList<Service> services;
    private TextView tvCurrentPage;
    private ImageView btnPreviousPage, btnNextPage;
    private int currentPage = 1;
    private int itemsPerPage = 6;
    private int totalPages;
    private ServiceListViewModel viewModel;

    public static ServiceListFragment newInstance() {
        ServiceListFragment fragment = new ServiceListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_service_list, container, false);

        tvCurrentPage = getActivity().findViewById(R.id.tvCurrentPage);
        btnPreviousPage = getActivity().findViewById(R.id.btnPreviousPage);
        btnNextPage = getActivity().findViewById(R.id.btnNextPage);

        tvCurrentPage.setText(String.valueOf(currentPage));

        btnPreviousPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePagination();
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                updatePagination();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ServiceListViewModel.class);

        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            adapter = new ServiceListAdapter(getActivity(), getActivity().getSupportFragmentManager(), services);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.fetchServices();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.fetchServices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroy();
    }

    private void updatePagination() {
        tvCurrentPage.setText(String.valueOf(currentPage));
        adapter.clear();
        adapter.addAll(getCurrentPageData());
        adapter.notifyDataSetChanged();
    }

    private ArrayList<Service> getCurrentPageData() {
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, services.size());
        return new ArrayList<>(services.subList(startIndex, endIndex));
    }
}
