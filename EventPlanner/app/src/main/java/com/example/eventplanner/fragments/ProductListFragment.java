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
import com.example.eventplanner.adapters.ProductListAdapter;
import com.example.eventplanner.viewmodels.ProductListViewModel;

public class ProductListFragment extends ListFragment {
    private ProductListAdapter adapter;
    private TextView currentPage;
    private ImageView btnPreviousPage, btnNextPage;
    private ProductListViewModel productListViewModel;

    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        currentPage = getActivity().findViewById(R.id.currentPage);
        btnPreviousPage = getActivity().findViewById(R.id.btnPreviousPage);
        btnNextPage = getActivity().findViewById(R.id.btnNextPage);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productListViewModel = new ViewModelProvider(requireActivity()).get(ProductListViewModel.class);
        productListViewModel.setContext(requireContext());

        adapter = new ProductListAdapter(requireActivity(), productListViewModel);
        setListAdapter(adapter);

        productListViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.updateProductsList(products);
            }
        });

        productListViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        setupPagination();
        fetchProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        productListViewModel.fetchProducts();
    }

    private void setupPagination() {
        btnPreviousPage.setOnClickListener(v -> {
            if (productListViewModel.getCurrentPage() > 1) {
                productListViewModel.setCurrentPage(productListViewModel.getCurrentPage() - 1);
                fetchProducts();
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (productListViewModel.getCurrentPage() < productListViewModel.getTotalPages()) {
                productListViewModel.setCurrentPage(productListViewModel.getCurrentPage() + 1);
                fetchProducts();
            }
        });
    }

    private void fetchProducts() {
        currentPage.setText(String.valueOf(productListViewModel.getCurrentPage()));
        productListViewModel.fetchProducts();
    }
}
