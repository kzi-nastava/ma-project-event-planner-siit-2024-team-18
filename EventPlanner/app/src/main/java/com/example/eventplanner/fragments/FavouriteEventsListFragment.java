package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AllEventsAdapter;
import com.example.eventplanner.viewmodels.AllEventsViewModel;

import java.util.ArrayList;

public class FavouriteEventsListFragment extends Fragment {
    private AllEventsAdapter adapter;
    private AllEventsViewModel eventsViewModel;
    private TextView noEventsTextView;

    public static FavouriteEventsListFragment newInstance() {
        return new FavouriteEventsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_events_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AllEventsAdapter(requireActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        eventsViewModel = new ViewModelProvider(requireActivity()).get(AllEventsViewModel.class);
        eventsViewModel.setContext(requireContext());
        noEventsTextView = view.findViewById(R.id.noEventsTextView);

        eventsViewModel.getFavouriteEvents().observe(getViewLifecycleOwner(), favouriteEvents -> {
            if (favouriteEvents != null && !favouriteEvents.isEmpty()) {
                adapter.updateEventList(favouriteEvents);
                noEventsTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                noEventsTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        eventsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        eventsViewModel.fetchFavouriteEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        eventsViewModel.getFavouriteEvents();
    }
}
