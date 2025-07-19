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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ActivityDetailsListAdapter;
import com.example.eventplanner.viewmodels.AgendaViewModel;

public class ActivityDetailsListFragment extends Fragment {
    private ActivityDetailsListAdapter adapter;
    private AgendaViewModel agendaViewModel;
    private int eventId;
    private TextView agenda, noAgenda;

    public static ActivityDetailsListFragment newInstance(AgendaViewModel agendaViewModel, int eventId) {
        ActivityDetailsListFragment fragment = new ActivityDetailsListFragment();
        fragment.setAgendaViewModelAndEvent(agendaViewModel, eventId);
        return fragment;
    }

    public void setAgendaViewModelAndEvent(AgendaViewModel agendaViewModel, int eventId) {
        this.agendaViewModel = agendaViewModel;
        this.eventId = eventId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_details_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ActivityDetailsListAdapter(requireActivity());
        recyclerView.setAdapter(adapter);

        agenda = view.findViewById(R.id.agenda);
        noAgenda = view.findViewById(R.id.noAgenda);

        agendaViewModel.getActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                adapter.updateActivityList(activities);
                agenda.setVisibility(View.VISIBLE);
                noAgenda.setVisibility(View.GONE);
            } else {
                agenda.setVisibility(View.GONE);
                noAgenda.setVisibility(View.VISIBLE);
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
