package com.example.eventplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.adapters.AllProductsServicesAdapter;
import com.example.eventplanner.models.ProductService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllProductsServicesFragment extends Fragment {

    private AllProductsServicesAdapter allProductsServicesAdapter;
    private List<ProductService> allProductsServicesList;
    private EditText searchEditText;

    private Button fromDateButton, toDateButton;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allProductsServicesList = new ArrayList<>();
        allProductsServicesList.add(new ProductService("Product 1", "Description for Product 1", R.drawable.product_service_placeholder2));
        allProductsServicesList.add(new ProductService("Service 1", "Description for Service 1", R.drawable.product_service_placeholder3));
        allProductsServicesList.add(new ProductService("Product 2", "Description for Product 2", R.drawable.product_service_placeholder3));
        allProductsServicesList.add(new ProductService("Product 3", "Description for Product 3", R.drawable.product_service_placeholder));
        allProductsServicesList.add(new ProductService("Service 2", "Description for Service 2", R.drawable.product_service_placeholder4));
        allProductsServicesList.add(new ProductService("Product 4", "Description for Product 4", R.drawable.product_service_placeholder4));
        allProductsServicesList.add(new ProductService("Product 5", "Description for Product 5", R.drawable.product_service_placeholder4));
        allProductsServicesList.add(new ProductService("Service 3", "Description for Service 3", R.drawable.product_service_placeholder));
        allProductsServicesList.add(new ProductService("Product 6", "Description for Product 6", R.drawable.product_service_placeholder));
        allProductsServicesList.add(new ProductService("Product 7", "Description for Product 7", R.drawable.product_service_placeholder3));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_products_services_section, container, false);

        RecyclerView allProductsServicesRecyclerView = view.findViewById(R.id.all_products_services_recycler_view);
        allProductsServicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allProductsServicesAdapter = new AllProductsServicesAdapter(getContext(), allProductsServicesList);
        allProductsServicesRecyclerView.setAdapter(allProductsServicesAdapter);

        Spinner sortSpinner = view.findViewById(R.id.sort_spinner);
        Spinner filterSpinner = view.findViewById(R.id.filter_spinner);
        searchEditText = view.findViewById(R.id.search_edit_text);
        ImageView searchIcon = view.findViewById(R.id.search_icon);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(R.layout.spinner_item);
        sortSpinner.setAdapter(sortAdapter);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(R.layout.spinner_item);
        filterSpinner.setAdapter(filterAdapter);

        searchIcon.setOnClickListener(v -> filterProductsServices(searchEditText.getText().toString()));
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                sortProductsServices(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                filterProductsServicesByType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        fromDateButton = view.findViewById(R.id.from_date_button);
        toDateButton = view.findViewById(R.id.to_date_button);
        calendar = Calendar.getInstance();

        fromDateButton.setOnClickListener(v -> openDatePickerDialog(fromDateButton));
        toDateButton.setOnClickListener(v -> openDatePickerDialog(toDateButton));

        return view;
    }

    private void openDatePickerDialog(final Button dateButton) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    dateButton.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void filterProductsServices(String query) {
        List<ProductService> filteredProductsServices = new ArrayList<>();
        for (ProductService productService : allProductsServicesList) {
            if (productService.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    productService.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredProductsServices.add(productService);
            }
        }
        allProductsServicesAdapter.updateProductServiceList(filteredProductsServices);
    }

    private void sortProductsServices(int sortType) {
        // Implement sorting logic for products/services
    }

    private void filterProductsServicesByType(int filterType) {
        // Implement filtering logic for products/services
    }
}
