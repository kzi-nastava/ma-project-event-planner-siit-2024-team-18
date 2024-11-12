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

import com.example.eventplanner.adapters.AllEventsAdapter;
import com.example.eventplanner.models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllEventsFragment extends Fragment {

    private AllEventsAdapter allEventsAdapter;
    private List<Event> allEventsList;
    private EditText searchEditText;
    private Button fromDateButton, toDateButton;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allEventsList = new ArrayList<>();
        allEventsList.add(new Event("Event 1", "Description for Event 1", R.drawable.event_placeholder4));
        allEventsList.add(new Event("Event 2", "Description for Event 2", R.drawable.event_placeholder));
        allEventsList.add(new Event("Event 3", "Description for Event 3", R.drawable.event_placeholder4));
        allEventsList.add(new Event("Event 4", "Description for Event 4", R.drawable.event_placeholder));
        allEventsList.add(new Event("Event 5", "Description for Event 5", R.drawable.event_placeholder));
        allEventsList.add(new Event("Event 1", "Description for Event 1", R.drawable.event_placeholder2));
        allEventsList.add(new Event("Event 2", "Description for Event 2", R.drawable.event_placeholder2));
        allEventsList.add(new Event("Event 3", "Description for Event 3", R.drawable.event_placeholder2));
        allEventsList.add(new Event("Event 4", "Description for Event 4", R.drawable.event_placeholder));
        allEventsList.add(new Event("Event 5", "Description for Event 5", R.drawable.event_placeholder));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_events_section, container, false);

        RecyclerView allEventsRecyclerView = view.findViewById(R.id.all_events_recycler_view);
        allEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allEventsAdapter = new AllEventsAdapter(getContext(), allEventsList);
        allEventsRecyclerView.setAdapter(allEventsAdapter);

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

        searchIcon.setOnClickListener(v -> filterEvents(searchEditText.getText().toString()));
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                sortEvents(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                filterEventsByType(position);
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

    private void filterEvents(String query) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : allEventsList) {
            if (event.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    event.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredEvents.add(event);
            }
        }
        allEventsAdapter.updateEventList(filteredEvents);
    }

    private void sortEvents(int sortType) {
        // Implement sorting logic for events
    }

    private void filterEventsByType(int filterType) {
        // Implement filtering logic for events
    }
}
