package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.CategoryListAdapter;
import com.example.eventplanner.adapters.EventTypeListAdapter;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;

public class EventTypeListFragment extends ListFragment {
    private EventTypeListAdapter adapter;
    private EventTypeCardViewModel eventTypeCardViewModel;
    private CategoryCardViewModel categoryCardViewModel;

    public static EventTypeListFragment newInstance() {
        return new EventTypeListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_type_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventTypeCardViewModel = new ViewModelProvider(requireActivity()).get(EventTypeCardViewModel.class);
        eventTypeCardViewModel.setContext(requireContext());

        categoryCardViewModel = new ViewModelProvider(requireActivity()).get(CategoryCardViewModel.class);
        categoryCardViewModel.setContext(requireContext());

        adapter = new EventTypeListAdapter(requireActivity(), eventTypeCardViewModel, categoryCardViewModel);
        setListAdapter(adapter);

        eventTypeCardViewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            if (eventTypes != null) {
                adapter.updateEventTypesList(eventTypes);
            }
        });

        eventTypeCardViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        eventTypeCardViewModel.fetchEventTypes();
    }

    @Override
    public void onResume() {
        super.onResume();
        eventTypeCardViewModel.getEventTypes();
    }
}
