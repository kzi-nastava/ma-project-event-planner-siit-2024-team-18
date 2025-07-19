package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ActivityListAdapter;
import com.example.eventplanner.viewmodels.AgendaViewModel;

public class ActivityListFragment extends ListFragment {
    private ActivityListAdapter adapter;
    private AgendaViewModel agendaViewModel;
    private int eventId;

    public static ActivityListFragment newInstance(AgendaViewModel agendaViewModel, int eventId) {
        ActivityListFragment fragment = new ActivityListFragment();
        fragment.setAgendaViewModelAndEvent(agendaViewModel, eventId);
        return fragment;
    }

    public void setAgendaViewModelAndEvent(AgendaViewModel agendaViewModel, int eventId) {
        this.agendaViewModel = agendaViewModel;
        this.eventId = eventId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ActivityListAdapter(requireActivity(), agendaViewModel, eventId);
        setListAdapter(adapter);

        agendaViewModel.getActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                adapter.updateActivityList(activities);
            }
        });

        agendaViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        agendaViewModel.fetchActivities(eventId);
    }

    @Override
    public void onResume() {
        super.onResume();
        agendaViewModel.getActivities();
    }
}
