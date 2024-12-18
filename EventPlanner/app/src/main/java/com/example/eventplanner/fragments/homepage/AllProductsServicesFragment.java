package com.example.eventplanner.fragments.homepage;

import android.app.DatePickerDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AllProductsServicesAdapter;
import com.example.eventplanner.databinding.AllProductsServicesSectionBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AllProductsServicesFragment extends Fragment implements SensorEventListener {

    private AllProductsServicesAdapter adapter;
    private AllProductsServicesSectionBinding binding;
    private AllProductsServicesViewModel viewModel;
    private Calendar calendar;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isSortingAscending = true;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 800;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AllProductsServicesSectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AllProductsServicesViewModel.class);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        injectFilterMapping();
        setupRecyclerView();
        setupSpinners();
        setupSearchFunctionality();
        setupDatePickers();
        setupPaginationButtons();
        setupRadioButtons();
        observeViewModel();
        viewModel.fetchSolutionCards();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (adapter == null || adapter.getItemCount() == 0) {
                return;
            }

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            long currentTime = System.currentTimeMillis();

            if (acceleration > 9 && (currentTime - lastShakeTime) > 2000) {
                lastShakeTime = currentTime;
                onShakeDetected();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void onShakeDetected() {
        isSortingAscending = !isSortingAscending;

        viewModel.setSelectedFilter("Price");

        String sortOrder = isSortingAscending ? "ASC" : "DESC";
        viewModel.setSortOrder(sortOrder);

        Toast.makeText(requireContext(),
                isSortingAscending ? "Sorted Products/Services by price: Ascending" : "Sorted Products/Services by price: Descending",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void injectFilterMapping() {
        Map<String, String> filterMapping = new HashMap<>();
        String[] labels = getResources().getStringArray(R.array.filter_options_products_services);
        String[] values = getResources().getStringArray(R.array.filter_values_products_services);
        for (int i = 0; i < labels.length; i++) {
            filterMapping.put(labels[i], values[i]);
        }
        viewModel.setFilterMapping(filterMapping);
    }

    private void setupRecyclerView() {
        adapter = new AllProductsServicesAdapter(getContext(), new ArrayList<>());
        binding.allProductsServicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.allProductsServicesRecyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_options_products_services,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.filterSpinner.setAdapter(filterAdapter);

        binding.filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLabel = parent.getItemAtPosition(position).toString();
                viewModel.setSelectedFilter(selectedLabel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.sortSpinner.setAdapter(sortAdapter);

        binding.sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortOrder = position == 0 ? "ASC" : "DESC";
                viewModel.setSortOrder(sortOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchFunctionality() {
        binding.searchIcon.setOnClickListener(v -> {
            String query = binding.searchEditText.getText().toString().trim();
            viewModel.setSearchQuery(query);
        });
    }

    private void setupDatePickers() {
        calendar = Calendar.getInstance();
        binding.fromDateButton.setOnClickListener(v -> openDatePickerDialog(binding.fromDateButton, true));
        binding.toDateButton.setOnClickListener(v -> openDatePickerDialog(binding.toDateButton, false));
    }

    private void openDatePickerDialog(Button dateButton, boolean isFromDate) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                requireContext(),
                (view, year1, month1, day1) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, day1);
                    dateButton.setText(formattedDate);
                    if (isFromDate) {
                        viewModel.setStartDate(formattedDate);
                    } else {
                        viewModel.setEndDate(formattedDate);
                    }
                },
                year, month, day
        ).show();
    }

    private void setupPaginationButtons() {
        binding.prevPageButton.setOnClickListener(v -> viewModel.goToPreviousPage());
        binding.nextPageButton.setOnClickListener(v -> viewModel.goToNextPage());
    }

    private void setupRadioButtons() {
        binding.showProductsRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setIsProductOnly(true);
            }
        });

        binding.showServicesRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setIsProductOnly(false);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getSolutionCards().observe(getViewLifecycleOwner(), solutionCards -> {
            if (solutionCards != null && !solutionCards.isEmpty()) {
                binding.noItemsMessage.setVisibility(View.GONE);
                binding.allProductsServicesRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateSolutionCardList(solutionCards);
            } else {
                binding.noItemsMessage.setVisibility(View.VISIBLE);
                binding.allProductsServicesRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingItemsMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getPageLiveData().observe(getViewLifecycleOwner(), currentPage -> {
            binding.currentPageButton.setText(String.format("%d / %d", currentPage + 1, viewModel.getTotalPages()));
            binding.prevPageButton.setEnabled(viewModel.hasPreviousPage());
            binding.nextPageButton.setEnabled(viewModel.hasNextPage());
        });
    }
}
