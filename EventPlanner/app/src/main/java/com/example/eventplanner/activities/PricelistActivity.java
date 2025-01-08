package com.example.eventplanner.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.PricelistListFragment;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.PricelistViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PricelistActivity extends BaseActivity {
    private ImageView createPDFButton, btnBack;
    private Button buttonProducts, buttonServices;
    private PricelistViewModel pricelistViewModel;
    private PricelistListFragment pricelistListFragment;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_pricelist, findViewById(R.id.content_frame));

        checkAndRequestPermissions();

        initializeViews();
        initializePricelistFragment();
        setupListeners();

        loadProducts();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initializeViews() {
        pricelistViewModel = new ViewModelProvider(this).get(PricelistViewModel.class);
        pricelistViewModel.setContext(this);

        createPDFButton = findViewById(R.id.createPDF);
        buttonProducts = findViewById(R.id.buttonProducts);
        buttonServices = findViewById(R.id.buttonServices);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializePricelistFragment() {
        pricelistListFragment = pricelistListFragment.newInstance(pricelistViewModel);
        FragmentTransition.to(pricelistListFragment, this, false, R.id.listViewPricelistItems);
    }

    private void setupListeners() {
        buttonProducts.setOnClickListener(v -> loadProducts());
        buttonServices.setOnClickListener(v -> loadServices());
        setupGetPDFPricelistButton();
        setupBackButton();
    }

    private void setupGetPDFPricelistButton() {
        createPDFButton.setOnClickListener(v -> {
            pricelistViewModel.getIsProductSelected().observe(this, isProductSelected -> {
                if (isProductSelected != null && isProductSelected) {
                    pricelistViewModel.getProducts().observe(this, products -> {
                        if (products != null && !products.isEmpty()) {
                            pricelistViewModel.generatePDF(convertProductsToMap(products));
                        } else {
                            Toast.makeText(this, "No products available for PDF generation.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    pricelistViewModel.getServices().observe(this, services -> {
                        if (services != null && !services.isEmpty()) {
                            pricelistViewModel.generatePDF(convertServicesToMap(services));
                        } else {
                            Toast.makeText(this, "No services available for PDF generation.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    private List<Map<String, Object>> convertProductsToMap(List<Product> products) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", product.getId());
            data.put("name", product.getName());
            data.put("price", (int) product.getPrice());
            data.put("discount", (int) product.getDiscount());
            dataList.add(data);
        }
        return dataList;
    }

    private List<Map<String, Object>> convertServicesToMap(List<Service> services) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Service service : services) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", service.getId());
            data.put("name", service.getName());
            data.put("price", (int) service.getPrice());
            data.put("discount", (int) service.getDiscount());
            dataList.add(data);
        }
        return dataList;
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void loadProducts() {
        buttonProducts.setEnabled(false);
        buttonServices.setEnabled(true);
        buttonProducts.setBackgroundColor(getResources().getColor(R.color.secondary));
        buttonServices.setBackgroundColor(getResources().getColor(R.color.selected));

        pricelistViewModel.setIsProductSelected(true);
        pricelistViewModel.getProducts();
    }

    private void loadServices() {
        buttonServices.setEnabled(false);
        buttonProducts.setEnabled(true);
        buttonServices.setBackgroundColor(getResources().getColor(R.color.secondary));
        buttonProducts.setBackgroundColor(getResources().getColor(R.color.selected));

        pricelistViewModel.setIsProductSelected(false);
        pricelistViewModel.getServices();
    }
}
