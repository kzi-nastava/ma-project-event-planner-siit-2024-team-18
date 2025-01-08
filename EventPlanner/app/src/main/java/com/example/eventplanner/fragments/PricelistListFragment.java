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
import com.example.eventplanner.adapters.PricelistListAdapter;
import com.example.eventplanner.viewmodels.PricelistViewModel;

public class PricelistListFragment extends ListFragment {
    private PricelistListAdapter adapter;
    private PricelistViewModel pricelistViewModel;

    public static PricelistListFragment newInstance(PricelistViewModel pricelistViewModel) {
        PricelistListFragment fragment = new PricelistListFragment();
        fragment.setPricelistViewModelAndEvent(pricelistViewModel);
        return fragment;
    }

    public void setPricelistViewModelAndEvent(PricelistViewModel PricelistViewModel) {
        this.pricelistViewModel = PricelistViewModel;
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

        adapter = new PricelistListAdapter(requireActivity(), pricelistViewModel);
        setListAdapter(adapter);

        pricelistViewModel.getIsProductSelected().observe(getViewLifecycleOwner(), isProductSelected -> {
            if (isProductSelected != null) {
                if (isProductSelected) {
                    pricelistViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
                        if (products != null) {
                            adapter.updatePricelistItemList(products);
                        }
                    });
                } else {
                    pricelistViewModel.getServices().observe(getViewLifecycleOwner(), services -> {
                        if (services != null) {
                            adapter.updatePricelistItemList(services);
                        }
                    });
                }
            }
        });

        pricelistViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        pricelistViewModel.fetchPricelistItems();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
