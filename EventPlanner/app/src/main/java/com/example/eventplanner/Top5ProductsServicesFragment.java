package com.example.eventplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.adapters.Top5ProductsServicesAdapter;
import com.example.eventplanner.models.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Top5ProductsServicesFragment extends Fragment {

    private RecyclerView top5ProductsServicesRecyclerView;
    private List<ProductService> top5ProductsServicesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        top5ProductsServicesList = new ArrayList<>();
        top5ProductsServicesList.add(new ProductService("Product 1", "Description for Product 1", R.drawable.product_service_placeholder));
        top5ProductsServicesList.add(new ProductService("Product 2", "Description for Product 2", R.drawable.product_service_placeholder));
        top5ProductsServicesList.add(new ProductService("Product 3", "Description for Product 3", R.drawable.product_service_placeholder2));
        top5ProductsServicesList.add(new ProductService("Product 4", "Description for Product 4", R.drawable.product_service_placeholder3));
        top5ProductsServicesList.add(new ProductService("Product 5", "Description for Product 5", R.drawable.product_service_placeholder2));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_5_products_services_carousel, container, false);

        top5ProductsServicesRecyclerView = view.findViewById(R.id.top_5_products_services_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        top5ProductsServicesRecyclerView.setLayoutManager(layoutManager);

        Top5ProductsServicesAdapter top5ProductsServicesAdapter = new Top5ProductsServicesAdapter(getContext(), top5ProductsServicesList);
        top5ProductsServicesRecyclerView.setAdapter(top5ProductsServicesAdapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(top5ProductsServicesRecyclerView);

        top5ProductsServicesAdapter.setSnapHelper(snapHelper);

        top5ProductsServicesAdapter.setRecyclerView(top5ProductsServicesRecyclerView);

        top5ProductsServicesRecyclerView.post(() -> top5ProductsServicesRecyclerView.scrollToPosition(0));

        top5ProductsServicesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View centerView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (centerView != null) {
                    int centerPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(centerView);

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        if (child != null) {
                            float scaleFactor = (recyclerView.getChildAdapterPosition(child) == centerPosition) ? 1.0f : 0.8f;
                            child.setScaleX(scaleFactor);
                            child.setScaleY(scaleFactor);
                        }
                    }
                }
            }
        });

        return view;
    }
}
