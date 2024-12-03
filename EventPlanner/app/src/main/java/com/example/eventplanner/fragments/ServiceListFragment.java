package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ServiceListAdapter;
import com.example.eventplanner.models.Service;

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

    public static ServiceListFragment newInstance(ArrayList<Service> services) {
        ServiceListFragment fragment = new ServiceListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, services);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            services = getArguments().getParcelableArrayList(ARG_PARAM);
            adapter = new ServiceListAdapter(getActivity(), services);
            setListAdapter(adapter);
        }
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
