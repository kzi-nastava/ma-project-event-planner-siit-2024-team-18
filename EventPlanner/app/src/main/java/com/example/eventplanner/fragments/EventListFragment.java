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
import com.example.eventplanner.adapters.EventListAdapter;
import com.example.eventplanner.viewmodels.EventListViewModel;

public class EventListFragment extends ListFragment {
    private EventListAdapter adapter;
    private EventListViewModel eventListViewModel;

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventListViewModel = new ViewModelProvider(requireActivity()).get(EventListViewModel.class);
        eventListViewModel.setContext(requireContext());

        adapter = new EventListAdapter(requireActivity(), eventListViewModel);
        setListAdapter(adapter);

        eventListViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                adapter.updateEventsList(events);
            }
        });

        eventListViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        fetchEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        eventListViewModel.fetchEvents();
    }

    private void fetchEvents() {
        eventListViewModel.fetchEvents();
    }
}
