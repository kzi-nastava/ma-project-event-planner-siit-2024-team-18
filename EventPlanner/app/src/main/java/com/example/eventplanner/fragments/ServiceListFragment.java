package com.example.eventplanner.fragments;

import android.os.Bundle;
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
import com.example.eventplanner.viewmodels.ServiceListViewModel;

public class ServiceListFragment extends ListFragment {
    private ServiceListAdapter adapter;
    private TextView currentPage;
    private ImageView btnPreviousPage, btnNextPage;
    private ServiceListViewModel serviceListViewModel;

    public static ServiceListFragment newInstance() {
        return new ServiceListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_list, container, false);

        currentPage = getActivity().findViewById(R.id.currentPage);
        btnPreviousPage = getActivity().findViewById(R.id.btnPreviousPage);
        btnNextPage = getActivity().findViewById(R.id.btnNextPage);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serviceListViewModel = new ViewModelProvider(requireActivity()).get(ServiceListViewModel.class);

        adapter = new ServiceListAdapter(requireActivity(), serviceListViewModel);
        setListAdapter(adapter);

        serviceListViewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                adapter.updateServicesList(services);
            }
        });

        serviceListViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        setupPagination();
        fetchServices();
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceListViewModel.fetchServices();
    }

    private void setupPagination() {
        btnPreviousPage.setOnClickListener(v -> {
            if (serviceListViewModel.getCurrentPage() > 1) {
                serviceListViewModel.setCurrentPage(serviceListViewModel.getCurrentPage() - 1);
                fetchServices();
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (serviceListViewModel.getCurrentPage() < serviceListViewModel.getTotalPages()) {
                serviceListViewModel.setCurrentPage(serviceListViewModel.getCurrentPage() + 1);
                fetchServices();
            }
        });
    }

    private void fetchServices() {
        currentPage.setText(String.valueOf(serviceListViewModel.getCurrentPage()));
        serviceListViewModel.fetchServices();
    }
}
