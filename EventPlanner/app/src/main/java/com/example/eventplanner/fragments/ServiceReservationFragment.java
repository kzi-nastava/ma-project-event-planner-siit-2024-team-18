package com.example.eventplanner.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.adapters.ServiceReservationAdapter;
import com.example.eventplanner.databinding.FragmentServiceReservationBinding;
import com.example.eventplanner.dialogs.RateProductDialog;
import com.example.eventplanner.dialogs.RateServiceDialog;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.ServiceDetailsViewModel;
import com.example.eventplanner.viewmodels.ServiceReservationViewModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationFragment extends Fragment {
    private FragmentServiceReservationBinding binding;
    private ServiceReservationViewModel viewModel;
    private final ServiceDetailsViewModel serviceDetailsViewModel;
    private final Service service;
    private ServiceReservationAdapter adapter;
    private LocalDate selectedDate;
    private LocalTime selectedFromTime;
    private LocalTime selectedToTime;
    private int selectedEventId;

    public ServiceReservationFragment(ServiceDetailsViewModel serviceDetailsViewModel, Service service) {
        this.serviceDetailsViewModel = serviceDetailsViewModel;
        this.service = service;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentServiceReservationBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ServiceReservationViewModel.class);
        viewModel.setContext(requireContext());

        setupRecyclerView();
        observeViewModel();
        setupEventSpinner();

        if (getArguments() != null) {
            int serviceId = getArguments().getInt("serviceId", -1);
            viewModel.setServiceId(serviceId);
            viewModel.fetchReservations(serviceId);
            viewModel.fetchServiceById(serviceId);
        }

        binding.selectDateButton.setOnClickListener(v -> showDatePickerDialog());
        binding.selectFromTimeButton.setOnClickListener(v -> showTimePickerDialog(true));
        binding.selectToTimeButton.setOnClickListener(v -> showTimePickerDialog(false));
        binding.chatLink.setOnClickListener(v -> chatWithServiceProvider());
        binding.bookServiceButton.setOnClickListener(v -> bookService());
        binding.closeButton.setOnClickListener(v -> navigateBackToServiceDetails());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceReservationAdapter(getContext());
        binding.reservationRecyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getReservations().observe(getViewLifecycleOwner(), reservations -> {
            if (reservations != null && !reservations.isEmpty()) {
                binding.noReservationsMessage.setVisibility(View.GONE);
                binding.reservationRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateReservations(reservations);
            } else {
                binding.noReservationsMessage.setVisibility(View.VISIBLE);
                binding.reservationRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingReservationsMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSelectedEventId().observe(getViewLifecycleOwner(), eventId -> {
            if (eventId != null) {
                selectedEventId = eventId;
            }
        });

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            if (date != null) {
                selectedDate = date;
            }
        });

        viewModel.getSelectedTimeFrom().observe(getViewLifecycleOwner(), timeFrom -> {
            if (timeFrom != null) {
                selectedFromTime = timeFrom;
            }
        });

        viewModel.getSelectedTimeTo().observe(getViewLifecycleOwner(), timeTo -> {
            if (timeTo != null) {
                selectedToTime = timeTo;
            }
        });

        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                List<String> eventNames = new ArrayList<>();
                for (EventCard event : events) {
                    eventNames.add(event.getName());
                }

                ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, eventNames);
                eventAdapter.setDropDownViewResource(R.layout.spinner_item);
                binding.eventSpinner.setAdapter(eventAdapter);
            }
        });
    }

    private void setupEventSpinner() {
        viewModel.fetchEvents();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            binding.selectDateButton.setText(selectedDate.toString());
            viewModel.setSelectedDate(selectedDate);
        }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
        datePickerDialog.show();
    }

    private void showTimePickerDialog(boolean isFromTime) {
        if (!isFromTime && viewModel.getServiceLiveData().getValue() != null &&
                viewModel.getServiceLiveData().getValue().getDuration() != 0) {
            Toast.makeText(getContext(), "To time is auto-calculated and cannot be modified.", Toast.LENGTH_SHORT).show();
            return;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            LocalTime time = LocalTime.of(hourOfDay, minute);
            if (isFromTime) {
                selectedFromTime = time;
                binding.selectFromTimeButton.setText(selectedFromTime.toString());
                viewModel.setSelectedTimeFrom(selectedFromTime);

                LocalTime calculatedToTime = viewModel.calculateToTime(selectedFromTime);
                if (calculatedToTime != null) {
                    selectedToTime = calculatedToTime;
                    binding.selectToTimeButton.setText(selectedToTime.toString());
                    binding.selectToTimeButton.setEnabled(false);
                    viewModel.setSelectedTimeTo(selectedToTime);
                } else {
                    binding.selectToTimeButton.setEnabled(true);
                }
            } else {
                selectedToTime = time;
                binding.selectToTimeButton.setText(selectedToTime.toString());
                viewModel.setSelectedTimeTo(selectedToTime);
            }
        }, 12, 0, true);
        timePickerDialog.show();
    }

    private void bookService() {
        String selectedEventName = (String) binding.eventSpinner.getSelectedItem();
        if (selectedEventName == null || selectedEventName.isEmpty()) {
            Toast.makeText(getContext(), "Please select an event.", Toast.LENGTH_SHORT).show();
            return;
        }

        EventCard selectedEvent = viewModel.getEventByName(selectedEventName);
        if (selectedEvent == null) {
            Toast.makeText(getContext(), "Selected event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedEventId = selectedEvent.getId();

        if (selectedDate == null || selectedFromTime == null || selectedToTime == null) {
            Toast.makeText(getContext(), "Please select a date and time.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!viewModel.isBookingValid(selectedEventId, selectedDate, selectedFromTime, selectedToTime)) {
            Toast.makeText(getContext(), "Invalid booking details. Please check the inputs.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.bookServiceButton.setText("Creating Reservation...");
        binding.bookServiceButton.setEnabled(false);

        viewModel.bookService(selectedEventId, selectedDate, selectedFromTime, selectedToTime, success -> {
            if (success) {
                Toast.makeText(getContext(), "Service booked successfully!", Toast.LENGTH_SHORT).show();
                viewModel.fetchReservations(viewModel.getServiceId());
                RateServiceDialog.show(getContext(), service, (value, comment) -> {
                    serviceDetailsViewModel.rateService(service.getId(), value, comment);
                }, this);

            } else {
                Toast.makeText(getContext(), "Failed to book service.", Toast.LENGTH_SHORT).show();
            }

            binding.bookServiceButton.setText("Book a Service");
            binding.bookServiceButton.setEnabled(true);
        });
    }

    private void navigateBackToServiceDetails() {
        Intent intent = new Intent(getContext(), ServiceDetailsActivity.class);
        intent.putExtra("solutionId", viewModel.getServiceId());
        startActivity(intent);
        getActivity().finish();
    }

    private void chatWithServiceProvider() {
        Toast.makeText(getContext(), "Chats coming soon!", Toast.LENGTH_SHORT).show();
    }
}
