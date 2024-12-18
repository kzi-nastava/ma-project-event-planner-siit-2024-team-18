package com.example.eventplanner.fragments.homepage;

import android.app.DatePickerDialog;
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
import com.example.eventplanner.adapters.AllEventsAdapter;
import com.example.eventplanner.databinding.AllEventsSectionBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AllEventsFragment extends Fragment {

    private AllEventsAdapter adapter;
    private AllEventsSectionBinding binding;
    private AllEventsViewModel viewModel;
    private Calendar calendar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AllEventsSectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AllEventsViewModel.class);

        injectFilterMapping();
        setupRecyclerView();
        setupSpinners();
        setupSearchFunctionality();
        setupDatePickers();
        setupPaginationButtons();
        observeViewModel();
        viewModel.fetchEvents();
    }

    private void injectFilterMapping() {
        Map<String, String> filterMapping = new HashMap<>();
        String[] labels = getResources().getStringArray(R.array.filter_options);
        String[] values = getResources().getStringArray(R.array.filter_values);
        for (int i = 0; i < labels.length; i++) {
            filterMapping.put(labels[i], values[i]);
        }
        viewModel.setFilterMapping(filterMapping);
    }

    private void setupRecyclerView() {
        adapter = new AllEventsAdapter(getContext(), new ArrayList<>());
        binding.allEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.allEventsRecyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_options,
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
                        viewModel.setFromDate(formattedDate);
                    } else {
                        viewModel.setToDate(formattedDate);
                    }
                },
                year, month, day
        ).show();
    }

    private void setupPaginationButtons() {
        binding.prevPageButton.setOnClickListener(v -> viewModel.goToPreviousPage());
        binding.nextPageButton.setOnClickListener(v -> viewModel.goToNextPage());
    }

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null && !events.isEmpty()) {
                binding.noEventsMessage.setVisibility(View.GONE);
                binding.allEventsRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateEventList(events);
            } else {
                binding.noEventsMessage.setVisibility(View.VISIBLE);
                binding.allEventsRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingEventsMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getPageLiveData().observe(getViewLifecycleOwner(), currentPage -> {
            binding.currentPageButton.setText(String.format("%d / %d", currentPage + 1, viewModel.getTotalPages()));
            binding.prevPageButton.setEnabled(viewModel.hasPreviousPage());
            binding.nextPageButton.setEnabled(viewModel.hasNextPage());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
